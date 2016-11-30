package com.ds2016;

/**
 * Created by wchee on 30/11/2016.
 */
public class Range<T extends Comparable<? super T>> {
    private final T mLower;
    private final T mUpper;

    public Range(final T lower, final T upper) {
        mLower = lower;
        mUpper = upper;
    }

    public static <T extends Comparable<? super T>> Range<T> create(final T lower, final T upper) {
        return new Range<T>(lower, upper);
    }

    public boolean contains(T value) {
        return value.compareTo(mLower) >= 0
                && value.compareTo(mUpper) <= 0;
    }

    public T getUpper() {
        return mUpper;
    }

    public T getLower() {
        return mLower;
    }
}
