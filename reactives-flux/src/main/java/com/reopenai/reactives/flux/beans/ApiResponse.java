package com.reopenai.reactives.flux.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reopenai.reactives.bean.error.ErrorCode;
import com.reopenai.reactives.core.i18n.I18nUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * Created by Allen Huang
 */
@Data
public class ApiResponse<T> {

    @Schema(description = "状态码.200表示成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private ErrorCode code;

    @Schema(description = "状态描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    private String message;

    @Schema(description = "数据内容")
    private T data;

    @Schema(description = "如果错误码是非200的状态,此参数返回的是错误描述的异常信息填充字段")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object[] vars;

    @SuppressWarnings("rawtypes")
    public static final ApiResponse EMPTY_SUCCESS = success(null);

    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> success() {
        return EMPTY_SUCCESS;
    }

    public static <T> Mono<ApiResponse<T>> mono(Mono<T> publisher) {
        return publisher
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.success());
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setData(data);
        result.setMessage("success");
        result.setCode(ErrorCode.Builtin.SUCCESS);
        return result;
    }

    public static <T> ApiResponse<T> failure(ErrorCode code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return failure(locale, code, args, locale);
    }

    public static <T> ApiResponse<T> failure(Locale locale, ErrorCode code, Object... args) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setVars(args);
        result.setCode(code);
        result.setMessage(I18nUtil.parseLocaleMessage(locale, code, args));
        return result;
    }

    public static <T> Mono<ApiResponse<T>> failureWithMono(ErrorCode code, Object... args) {
        return Mono.deferContextual(ctx -> {
            Locale locale = ctx.getOrDefault(Locale.class, Locale.SIMPLIFIED_CHINESE);
            ApiResponse<T> result = new ApiResponse<>();
            result.setVars(args);
            result.setCode(code);
            result.setMessage(I18nUtil.parseLocaleMessage(locale, code, args));
            return Mono.just(result);
        });
    }

    public static <T> ApiResponse<T> failureWithMessage(ErrorCode code, String message) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setMessage(message);
        result.setCode(code);
        return result;
    }

}
