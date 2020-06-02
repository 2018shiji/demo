package com.example.demo;

@FunctionalInterface
public interface FunctionFormatter<TValue, TRow, TIndex, R> {
    R apply(TValue value, TRow row, TIndex index);
}
