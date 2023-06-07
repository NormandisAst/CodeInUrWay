package PACK;
public class A extends D {
	public byte a1;
	public byte geta1() {
		return a1;
	}
	public void seta1(byte a1) {
		this.a1 = a1;
	}
	private C C;
	public C getC() {
		return C;
	}
	public A(C C) {
		this.C = C;
	}
	private B B;
	public B getB() {
		return B;
	}
	public void setB(B B) {
		this.B = B;
	}
}