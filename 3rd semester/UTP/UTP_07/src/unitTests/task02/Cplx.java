package unitTests.task02;

public
class Cplx {

    private double re;
    private double im;

    public Cplx(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Cplx add(Cplx num){
        return new Cplx(
            this.re + num.re,
            this.im + num.im
        );
    }

    public Cplx addAss(Cplx num){
        this.re += num.re;
        this.im += num.im;
        return this;
    }

    public Cplx sub(Cplx num){
        return new Cplx(
            this.re - num.re,
            this.im - num.im
        );
    }

    public Cplx subAss(Cplx num){
        this.re -= num.re;
        this.im -= num.im;
        return this;
    }

    public Cplx mul(Cplx num){
        return new Cplx(
            (this.re * num.re) - (this.im * num.im),
            (this.im * num.re) + (this.re * num.im)
        );
    }

    public Cplx mulAss(Cplx num){
        double tmpRe = (this.re * num.re) - (this.im * num.im);
        double tmpIm = (this.im * num.re) + (this.re * num.im);
        this.re = tmpRe;
        this.im = tmpIm;
        return this;
    }

    public double[] getCplxArr(){
        return new double[]{ this.re, this.im};
    }

    public String toString(){
        return this.re + " + " + this.im + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cplx cplx = (Cplx) o;

        if (Double.compare(re, cplx.re) != 0) return false;
        return Double.compare(im, cplx.im) == 0;
    }
}