package sample;

/**
 * 中心からのオフセットと重みを保持するクラス
 */
public class Weight {
	public final int offsetX;
	public final int offsetY;
	public final double weight;
	
	public Weight(int x, int y, double w){
		offsetX = x;
		offsetY = y;
		weight = w;
	}
}
