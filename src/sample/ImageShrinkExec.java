package sample;

import java.awt.image.BufferedImage;
import sample.ImageShrinkImpl;

/**
 * 画像縮小実行サンプル
 *
 */
public class ImageShrinkExec {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// 元画像ファイル
			BufferedImage base = ImageShrinkImpl.loadImage("/Hogehoge/ImageShrink/base.jpg");
			int dw = 120, dh= 90;
			int dw2_1 = 640, dh2_1 = 480;
			int dw3_1 = 800, dh3_1 = 600, dw3_2 = 480, dh3_2 = 360;
			
			// ↓縮小実行パターンいろいろ
			BufferedImage reduce1_1 = ImageShrinkImpl.reduce1(base, dw, dh);
			ImageShrinkImpl.outputImage("reduce1_1.jpg", reduce1_1);
			
			BufferedImage reduce2_1 = ImageShrinkImpl.reduce2(base, dw, dh);
			ImageShrinkImpl.outputImage("reduce2_1.jpg", reduce2_1);
			
			ImageShrinkImpl.outputImage("reduce1_2.jpg", ImageShrinkImpl.reduce1(ImageShrinkImpl.reduce1(base, dw2_1, dh2_1), dw, dh));
			 
			ImageShrinkImpl.outputImage("reduce2_2.jpg", ImageShrinkImpl.reduce2(ImageShrinkImpl.reduce2(base, dw2_1, dh2_1), dw, dh));
			 
			ImageShrinkImpl.outputImage("reduce1_3.jpg", ImageShrinkImpl.reduce1(ImageShrinkImpl.reduce1(ImageShrinkImpl.reduce1(base, dw3_1, dh3_1), dw3_2, dh3_2), dw, dh));
			 
			ImageShrinkImpl.outputImage("reduce2_3.jpg", ImageShrinkImpl.reduce2(ImageShrinkImpl.reduce2(ImageShrinkImpl.reduce2(base, dw3_1, dh3_1), dw3_2, dh3_2), dw, dh));
			
			BufferedImage reduce3_1 = ImageShrinkImpl.reduce3(base, 2, 120, 90);
			ImageShrinkImpl.outputImage("reduce3_1.jpg", reduce3_1);
			 
			BufferedImage reduce3_2 = ImageShrinkImpl.reduce3(base, 3, 120, 90);
			ImageShrinkImpl.outputImage("reduce3_2.jpg", reduce3_2);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}

