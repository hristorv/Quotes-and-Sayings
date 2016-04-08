package tk.example.quotesandsayings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SlideFrameLayout extends FrameLayout {

	public SlideFrameLayout(Context context) {
		super(context);
	}

	public SlideFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public float getXFraction() {
		if (getWidth() == 0) {
			return 0;
		}
		return getTranslationX() / getWidth();
	}

	public void setXFraction(float xFraction) {
		float translationX = getWidth() * xFraction;
		setTranslationX(translationX);
	}

}
