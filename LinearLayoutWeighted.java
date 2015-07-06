package com.anywayanyday.android.view.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

// only horizontal now
// use with MATCH_PARENT or exact width attribute
public class LinearLayoutWeighted extends ViewGroup {

	private boolean mWeightsEquals = false;

	public LinearLayoutWeighted(Context context) {
		super(context);
	}

	public LinearLayoutWeighted(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(attrs);
	}

	public LinearLayoutWeighted(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(attrs);
	}

	private void initAttrs(AttributeSet attrs) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LinearLayoutWeighted);
		if (a != null) {
			mWeightsEquals = a.getBoolean(R.styleable.LinearLayoutWeighted_weightsEquals, false);
			a.recycle();
		}
	}

	private int getWidthMargins(MarginLayoutParams lp) {
		return lp.leftMargin + lp.rightMargin;
	}

	private int getMeasuredWidthWithMargins(View view, MarginLayoutParams lp) {
		return view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
	}

	private int getMeasuredHeightWithMargins(View view, MarginLayoutParams lp) {
		return view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int width = parentWidth - getPaddingLeft() - getPaddingRight();

		int maxHeight = 0;
		float weightSum = 0;

		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View view = getChildAt(i);
			if (view.getVisibility() != GONE) {
				final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view.getLayoutParams();
				if (mWeightsEquals && lp.weight == 0) {
					lp.weight = 1;
				} else if (lp.weight == 0) {
					final int viewWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width - getWidthMargins(lp), MeasureSpec.AT_MOST);
					view.measure(viewWidthMeasureSpec, heightMeasureSpec);

					maxHeight = Math.max(maxHeight, getMeasuredHeightWithMargins(view, lp));

					width -= view.getMeasuredWidth();
				}
				weightSum += lp.weight;
			}
		}

		for (int i = 0; i < childCount; i++) {
			final View view = getChildAt(i);
			if (view.getVisibility() != GONE) {
				final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view.getLayoutParams();
				if (lp.weight != 0) {
					final int viewWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int)(width * lp.weight / weightSum) - getWidthMargins(lp), MeasureSpec.EXACTLY);
					view.measure(viewWidthMeasureSpec, heightMeasureSpec);

					maxHeight = Math.max(maxHeight, getMeasuredHeightWithMargins(view, lp));
				}
			}
		}

		setMeasuredDimension(parentWidth, maxHeight + getPaddingTop() + getPaddingBottom());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		int leftPos = getPaddingLeft();
		final int topPos = getPaddingTop();

		final int childrenCount = getChildCount();
		for (int i = 0; i < childrenCount; i++) {
			final View view = getChildAt(i);
			if (view.getVisibility() != GONE) {
				final MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();

				view.layout(leftPos + lp.leftMargin, topPos + lp.topMargin,
							leftPos + lp.leftMargin + view.getMeasuredWidth(),
							topPos + lp.topMargin + view.getMeasuredHeight());
				leftPos += lp.leftMargin + view.getMeasuredWidth() + lp.rightMargin;
			}
		}
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LinearLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LinearLayout.LayoutParams(p);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LinearLayout.LayoutParams;
	}
}
