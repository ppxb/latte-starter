package com.ppxb.latte.starter.security.mask.strategy;

public interface IMaskStrategy {

    String mask(String str, char character, int left, int right);
}
