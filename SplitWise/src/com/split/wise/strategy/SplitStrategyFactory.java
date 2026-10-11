package com.split.wise.strategy;

public class SplitStrategyFactory {
    public static SplitStrategy getStrategy(SplitType type) {
        if (type == null) return null;
        switch (type) {
        case EQUAL:
            return new EqualSplitStrategy();
        case EXACT:
            return new ExactSplitStrategy();
        case PERCENTAGE:
            return new PercentageSplitStrategy();
        case RATIO:
            return new RatioSplitStrategy();
        default:
            return null;
        }
    }
}
