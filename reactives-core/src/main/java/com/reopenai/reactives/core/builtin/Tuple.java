package com.reopenai.reactives.core.builtin;

/**
 * Created by Allen Huang
 */
public interface Tuple {

    record V2<V1, V2>(V1 v1, V2 v2) implements Tuple {

    }

    record V3<V1, V2, V3>(V1 v1, V2 v2, V3 v3) implements Tuple {

    }

    record V4<V1, V2, V3, V4>(V1 v1, V2 v2, V3 v3, V4 v4) implements Tuple {
    }

    record V5<V1, V2, V3, V4, V5>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) implements Tuple {

    }

    record V6<V1, V2, V3, V4, V5, V6>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) implements Tuple {

    }

    record V7<V1, V2, V3, V4, V5, V6, V7>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) implements Tuple {

    }

    record V8<V1, V2, V3, V4, V5, V6, V7, V8>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) implements Tuple {

    }

    record V9<V1, V2, V3, V4, V5, V6, V7, V8, V9>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8,
                                                  V9 v9) implements Tuple {

    }

    record V10<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10>(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9,
                                                        V10 v10) implements Tuple {

    }


}
