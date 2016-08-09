package sample;

/**
 * 重みの一覧と合計を保持するクラス
 */
public class WeightFilter {
	public final double weightTotal;
	public final Weight[] weightList;
	
	public WeightFilter(double sumw, Weight[] weights){
		weightTotal = sumw;
		weightList = weights;
	}
}
