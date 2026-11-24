package unitTests.task01;

public
	class SimpleNumericalOperations{

    // FIX: was "a - b"
    public int add(int a, int b){
        return a + b;
    }

    // FIX: was "value < 0" (that checks for negative numbers)
    public boolean isPositive(int value){
        return value > 0;
    }

    // FIX: was "value >= 0" (wrong sign, and 0 is not negative)
    public boolean isNegative(int value){
        return value < 0;
    }
}
