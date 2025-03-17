package com.reopenai.reactives.core.i18n;

import cn.hutool.core.util.StrUtil;
import com.reopenai.reactives.bean.enums.Language;
import com.reopenai.reactives.core.builtin.Tuple;
import com.reopenai.reactives.core.cache.local.LocalCache;
import com.reopenai.reactives.core.cache.local.LocalCacheInvoker;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Locale;

/**
 * @author Allen Huang
 */
public class RedisMessageSource extends ResourceBundleMessageSource {

    private static final MessageFormat NULL_VALUE = new MessageFormat("");

    private final LocalCacheInvoker.F1<Tuple.V2<Language, String>, MessageFormat> localCache;

    public RedisMessageSource(StringRedisTemplate redisTemplate) {
        this.localCache = LocalCache.createInvokerWithTuple("system.i18n", 64, 512, Duration.ofMinutes(15), Duration.ofMinutes(10), tuple -> {
            Language language = tuple.v1();
            String code = tuple.v2();
            String msgCode = String.format("SYSTEM:I18N:%s", language.getDisplayCode());
            String value = redisTemplate.<String, String>opsForHash().get(msgCode, code);
            if (StrUtil.isNotBlank(value)) {
                return new MessageFormat(value);
            }
            return NULL_VALUE;
        });
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Language language = Language.fromLocale(locale);
        MessageFormat messageFormat = localCache.get(new Tuple.V2<>(language, code));
        if (messageFormat == NULL_VALUE) {
            Language subLanguage = Language.fromLocale(new Locale(locale.getLanguage()));
            if (subLanguage != language) {
                messageFormat = localCache.get(new Tuple.V2<>(subLanguage, code));
                if (messageFormat != NULL_VALUE) {
                    localCache.put(new Tuple.V2<>(language, code), messageFormat);
                    return messageFormat;
                }
            }
            return null;
        }
        return messageFormat;
    }

    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        MessageFormat messageFormat = resolveCode(code, locale);
        if (messageFormat != null) {
            synchronized (messageFormat) {
                return messageFormat.format(new Object[0]);
            }
        }
        return null;
    }

}
