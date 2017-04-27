package sample;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

/**
 * 画像縮小処理パターン
 * @author onizawa_no
 * 
 */
public class ImageShrinkImpl {

	/**
	 *【縮小処理】パターン１
	 * @param image dw dh
	 */
	public static BufferedImage reduce1(BufferedImage image, int dw, int dh){
		
		// 
		BufferedImage thumb = new BufferedImage(dw, dh, image.getType());
		thumb.getGraphics().drawImage(image.getScaledInstance(dw, dh, Image.SCALE_AREA_AVERAGING), 0, 0, dw, dh, null);

		return thumb;

	}
	
	/**
	 *【縮小処理】パターン２
	 * @param image dw dh
	 */
	public static BufferedImage reduce2(BufferedImage image, int dw, int dh) {

		// 
		BufferedImage thumb = new BufferedImage(dw, dh, image.getType());
		
		Graphics2D g2d = thumb.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2d.drawImage(image, 0, 0, dw, dh, null);
		
		return thumb;
		}	
	
	/**
	 * 【縮小処理】パターン３
	 * @param
	 */
	public static BufferedImage reduce3(BufferedImage image, int n, int dw, int dh){

		int sw = image.getWidth(), sh = image.getHeight();
		double scale = (double)dw / sw;
		BufferedImage thumb = new BufferedImage(dw, dh, image.getType());
		WeightFilter filter = getReduceFilter(scale, n);
		double sumw = filter.weightTotal;
		int[] colors = image.getRGB(0, 0, sw, sh, null, 0, sw);
	 
		for (int dy = 0; dy < dh; ++dy) {
			for (int dx = 0; dx < dw; ++dx) {
				double dr = 0.0, db = 0.0, dg = 0.0;
				int bsx = (int)(dx / scale), bsy = (int)(dy / scale);
				for (Weight weight : filter.weightList) {
					int rgb = getRGB(colors, bsx + weight.offsetX, bsy + weight.offsetY, sw, sh);
					dr += ((rgb >> 16) & 0xFF) * weight.weight;
					dg += ((rgb >> 8) & 0xFF) * weight.weight;
					db += (rgb & 0xFF) * weight.weight;
				}
				thumb.setRGB(dx, dy, (getColor(dr, sumw) << 16) | (getColor(dg, sumw) << 8) | getColor(db, sumw));
			}
		}
		return thumb;
	}

	/**
	 * lanczos関数
	 */
	public static double lanczos(double x, double n){
		if (x == 0.0) return 1.0;
		if (Math.abs(x) >= n) return 0.0;
		return Math.sin(Math.PI * x) / Math.PI / x * (Math.sin(Math.PI * x / n) / Math.PI / x * n);
	}
	
	/**
	 * 縮小率と距離で重み一覧を作成
	 */
	public static WeightFilter getReduceFilter(double scale, int distance) {

		double bdx0 = 0 + 0.5, bdy0 = 0 + 0.5;
		int ss = (int)((bdx0 - distance) / scale), se = (int)((bdx0 + distance) / scale);
		double sumw = 0.0;
		List<Weight> weightList = new ArrayList<>((se - ss) * (se - ss));
		 
		for (int sy = ss; sy <= se; ++sy) {
		  for (int sx = ss; sx <= se; ++sx) {
		      double xl = (sx + 0.5) * scale - bdx0;
		      double yl = (sy + 0.5) * scale - bdy0;
		      double w = lanczos(Math.sqrt(xl * xl + yl * yl), distance);
		      if (w == 0.0) continue;
		      weightList.add(new Weight(sx, sy, w));
		      sumw += w;
		  }
		}
		return new WeightFilter(sumw, weightList.toArray(new Weight[0]));
	}
	
	/**
	 * 指定座標のRGB値取得
	 */
	public static int getRGB(int[] image, int x, int y, int w, int h) {
		x = Math.abs(x); y = Math.abs(y);
		if (x >= w) x = w + w - x - 1;
		if (y >= h) y = h + h - y - 1;
		return image[y * w + x];
	}
	
	/**
	 * 0 - 255の範囲で色値取得
	 */
	public static int getColor(double val, double sumw){
		if (sumw != 0.0) val /= sumw;
		if (val < 0.0) return 0;
		if (val > 255.0) return 255;
		return (int)val;
	}
	
	/**
	 *【共通処理】画像読み込み
	 * @param filename
	 */
	public static BufferedImage loadImage(String filename) throws IOException{
		
		try(FileInputStream in = new FileInputStream(filename)){

			return ImageIO.read(in);
		}
	}
	
	/**
	 * 【共通処理】画像出力
	 *  @param filename image
	 */
	public static void outputImage(String filename, BufferedImage image) throws IOException{
		
		JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.95f);
		
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(ImageIO.createImageOutputStream(new File(filename)));
		writer.write(null, new IIOImage(image, null, null), param);
		writer.dispose();
		
	}
	
}
