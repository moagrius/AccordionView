package com.qozix.widgets;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class AccordionView extends ScrollView {
	
	private static final String TAG = "AccordionView";

	private static final int SCROLL_ANIMATION_DURATION = 500;
	private static final int OPEN_TRANSITION_DURATION = 500;

	private LinearLayout linearLayout;
	
	private ValueAnimator animator = null;
	private OnAccordionEventListener listener = null;

	private int scrollAnimationDuration = SCROLL_ANIMATION_DURATION;
	private int openTransitionDuration = OPEN_TRANSITION_DURATION;
	
	private TimeInterpolator openTransitionInterpolator = new AccelerateDecelerateInterpolator();
	private TimeInterpolator scrollInterpolator = new AccelerateDecelerateInterpolator();
	
	private boolean scrollToTopOnItemOpen = true;
	private boolean allowMultipleItemsOpen = true;
	private boolean scrollAnimationEnabled = true;
	private boolean openTransitionEnabled = true;
	
	private int lastScrollPosition;
	
	private AccordionView.Item activeItem = null;

	public AccordionView( Context context ) {
		super( context );
		linearLayout = new LinearLayout( context );
		linearLayout.setOrientation( LinearLayout.VERTICAL );
		LayoutParams lp = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		addView( linearLayout, lp );
	}

	public LinearLayout getLinearLayout(){
		return linearLayout;
	}
	
	public AccordionView.Item addItem( View titleView, View contentView ) {
		AccordionView.Item item = new AccordionView.Item( this );
		item.setViews( titleView, contentView );
		return addItem( item );
	}
	
	public AccordionView.Item addItem( AccordionView.Item item ) {
		LinearLayout.LayoutParams itemLayout = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		linearLayout.addView( item, itemLayout );
		return item;
	}

	public void removeItem( AccordionView.Item item ) {
		if ( linearLayout.indexOfChild( item ) > -1 ) {
			linearLayout.removeView( item );
		}
	}
	
	public void removeAllItems(){
		linearLayout.removeAllViews();
	}

	public boolean getAllowMultipleItemsOpen() {
		return allowMultipleItemsOpen;
	}

	public void setAllowMultipleItemsOpen( boolean a ) {
		allowMultipleItemsOpen = a;
	}

	public TimeInterpolator getScrollInterpolator() {
		return scrollInterpolator;
	}

	public void setScrollInterpolator( TimeInterpolator i ) {
		scrollInterpolator = i;
	}
	
	public TimeInterpolator getOpenTransitionInterpolator() {
		return openTransitionInterpolator;
	}

	public void setOpenTransitionInterpolator( TimeInterpolator i ) {
		openTransitionInterpolator = i;
	}

	public int getScrollAnimationDuration() {
		return scrollAnimationDuration;
	}

	public void setScrollAnimationDuration( int s ) {
		scrollAnimationDuration = s;
	}

	public int getOpenTransitionDuration() {
		return openTransitionDuration;
	}

	public void setOpenTransitionDuration( int t ) {
		openTransitionDuration = t;
	}

	public boolean getScrollAnimationEnabled() {
		return scrollAnimationEnabled;
	}

	public void setScrollAnimationEnabled( boolean e ) {
		scrollAnimationEnabled = e;
	}

	public boolean getOpenTransitionEnabled() {
		return openTransitionEnabled;
	}

	public void setOpenTransitionEnabled( boolean e ) {
		openTransitionEnabled = e;
	}

	public boolean getScrollToTopOnItemOpen() {
		return scrollToTopOnItemOpen;
	}

	public void setScrollToTopOnItemOpen( boolean s ) {
		scrollToTopOnItemOpen = s;
	}
	
	public void setOnAccordionEventListener( OnAccordionEventListener l ) {
		listener = l;
	}

	public void closeAllItems() {
		for ( int i = 0; i < linearLayout.getChildCount(); i++ ) {
			View child = linearLayout.getChildAt( i );
			if ( child instanceof AccordionView.Item ) {
				AccordionView.Item item = (AccordionView.Item) child;
				item.close();
			}
		}
	}

	private void closeAllExceptActiveItem() {
		for ( int i = 0; i < linearLayout.getChildCount(); i++ ) {
			View child = linearLayout.getChildAt( i );
			if ( child instanceof AccordionView.Item ) {
				AccordionView.Item item = (AccordionView.Item) child;
				if ( item != activeItem ) {
					item.close();
				}
			}
		}
	}
	
	private void scrollToItem(){
		if ( scrollAnimationEnabled ) {
			scrollWithAnimation();
		} else {
			scrollWithoutAnimation();
		}
	}
	
	private void scrollWithAnimation(){
		lastScrollPosition = getScrollY();
		animator = ValueAnimator.ofFloat( 0f, 1f );			
		animator.setDuration( openTransitionDuration );
		animator.setInterpolator( scrollInterpolator );
		animator.addUpdateListener( animatorUpdateListener );
		animator.start();
	}
	
	private void scrollWithoutAnimation(){
		post( immediateScrollRunnable );
	}
	
	private void setActiveItem( AccordionView.Item item ) {
		activeItem = item;
		if ( !allowMultipleItemsOpen ) {
			closeAllExceptActiveItem();
		}
		if ( animator != null ) {
			animator.cancel();
		}		
		if ( scrollToTopOnItemOpen ) {
			scrollToItem();
		}
	}
	
	private void onItemStartOpen( AccordionView.Item item ) {
		setActiveItem( item );
		if ( listener != null ) {
			listener.onItemStartOpen( item );
		}
	}
	
	private void onItemStartClose( AccordionView.Item item ) {
		if ( listener != null ) {
			listener.onItemStartClose( item );
		}
	}
	
	private void onItemClosed( AccordionView.Item item ) {
		if ( listener != null ) {
			listener.onItemClosed( item );
		}
	}

	private void onItemOpened( AccordionView.Item item ) {
		if ( listener != null ) {
			listener.onItemOpened( item );
		}
	}

	private Runnable immediateScrollRunnable = new Runnable(){
		@Override
		public void run(){
			if( activeItem != null ) {
				scrollTo( 0, activeItem.getTop() );
			}
		}
	};
	
	private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate( ValueAnimator animation ) {
			if ( activeItem != null ) {
				Float delta = (Float) animation.getAnimatedValue();
				int y = (int) ( lastScrollPosition + ( activeItem.getTop() - lastScrollPosition ) * delta );
				scrollTo( 0, y );
			}
		}
	};

	public static interface OnAccordionEventListener {
		public void onItemStartOpen( Item item );
		public void onItemStartClose( Item item );
		public void onItemClosed( Item item );
		public void onItemOpened( Item item );
	}

	public static class Item extends ViewGroup {

		private static final float FULLY_OPEN_DEGREE = 1f;
		private static final float FULLY_CLOSED_DEGREE = 0f;
		
		private static enum OPEN_STATES {
			FULLY_OPEN,
			FULLY_CLOSED,
			PARTIALLY_OPEN
		}
	
		private AccordionView accordion;

		private View titleView;
		private View contentView;

		private float actualDegreeOpen = 0f;
		private float desiredDegreeOpen = 0f;
		
		private OPEN_STATES openState = OPEN_STATES.FULLY_CLOSED;

		private ValueAnimator animator = null;

		public Item( AccordionView a ) {
			super( a.getContext() );
			accordion = a;
		}

		public AccordionView getAccordion() {
			return accordion;
		}

		public void setViews( View title, View content ) {
			setTitleView( title );
			setContentView( content );
		}

		public void setTitleView( View view ) {
			if ( titleView != null && indexOfChild( titleView ) > -1 ) {
				removeView( titleView );
			}
			titleView = view;
			titleView.setOnClickListener( toggleContentListener );
			addView( titleView );
		}

		public void setContentView( View view ) {
			if ( contentView != null && indexOfChild( contentView ) > -1 ) {
				removeView( contentView );
			}
			contentView = view;
			addView( contentView );
		}

		public View getTitleView() {
			return titleView;
		}

		public View getContentView() {
			return contentView;
		}

		public boolean isFullyOpen() {
			return openState == OPEN_STATES.FULLY_OPEN;
		}

		public boolean isFullyClosed() {
			return openState == OPEN_STATES.FULLY_CLOSED;
		}

		public boolean isPartiallyOpen() {
			return openState == OPEN_STATES.PARTIALLY_OPEN;
		}

		public boolean isPartiallyClosed() {
			return openState == OPEN_STATES.PARTIALLY_OPEN;
		}
		
		
		private void setActualDegreeOpen( float o ) {
			actualDegreeOpen = o;
			if ( actualDegreeOpen == FULLY_CLOSED_DEGREE ) {
				if( openState != OPEN_STATES.FULLY_CLOSED ) {
					accordion.onItemClosed( this );
				}
				openState = OPEN_STATES.FULLY_CLOSED;
			} else if ( actualDegreeOpen == FULLY_OPEN_DEGREE ) {
				if( openState != OPEN_STATES.FULLY_OPEN ) {
					accordion.onItemOpened( this );
				}
				openState = OPEN_STATES.FULLY_OPEN;				
			} else {
				openState = OPEN_STATES.PARTIALLY_OPEN;
			}
			requestLayout();
		}

		public void setDegreeOpen( float o, boolean animate ) {
			desiredDegreeOpen = o;
			if ( desiredDegreeOpen == 0f ) {
				if( openState != OPEN_STATES.FULLY_CLOSED ) {
					accordion.onItemStartClose( this );
				}
			}
			if ( desiredDegreeOpen == 1f ) {
				if( openState != OPEN_STATES.FULLY_OPEN ) {
					accordion.onItemStartOpen( this );
				}
			}
			if ( animator != null ) {
				animator.cancel();
			}
			if ( animate && accordion.getOpenTransitionEnabled() ) {
				animator = ValueAnimator.ofFloat( actualDegreeOpen, desiredDegreeOpen );
				animator.setDuration( accordion.getOpenTransitionDuration() );
				animator.setInterpolator( accordion.getOpenTransitionInterpolator() );
				animator.addUpdateListener( animatorUpdateListener );
				animator.start();
			} else {
				setActualDegreeOpen( o );
			}
		}

		public void open( boolean animate ) {
			setDegreeOpen( 1f, animate );
		}

		public void open() {
			open( accordion.getOpenTransitionEnabled() );
		}

		public void close( boolean animate ) {
			setDegreeOpen( 0f, animate );
		}

		public void close() {
			close( accordion.getOpenTransitionEnabled() );
		}

		public void toggle( boolean animate ) {
			if( isFullyOpen() ) {
				close( animate );
			} else if ( isFullyClosed() ) {
				open( animate );
			}
		}

		@Override
		protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

			measureChildren( widthMeasureSpec, heightMeasureSpec );

			int width = 0;
			int height = 0;

			if ( titleView != null ) {
				width = titleView.getMeasuredWidth();
				height = titleView.getMeasuredHeight();
			}

			if ( contentView != null ) {
				width = Math.max( width, contentView.getMeasuredWidth() );
				height += contentView.getMeasuredHeight() * actualDegreeOpen;
			}

			width = Math.max( width, getSuggestedMinimumWidth() );
			height = Math.max( height, getSuggestedMinimumWidth() );

			width = resolveSize( width, widthMeasureSpec );
			height = resolveSize( height, heightMeasureSpec );

			setMeasuredDimension( width, height );

		}

		@Override
		protected void onLayout( boolean changed, int l, int t, int r, int b ) {
			int titleHeight = 0;
			if ( titleView != null ) {
				titleHeight = titleView.getMeasuredHeight();
				titleView.layout( 0, 0, r, titleHeight );
			}
			if ( contentView != null ) {
				int contentHeight = (int) ( contentView.getMeasuredHeight() * actualDegreeOpen );
				contentView.layout( 0, titleHeight, r, titleHeight + contentHeight );
			}
		}

		private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate( ValueAnimator animation ) {
				Float howOpen = (Float) animation.getAnimatedValue();
				setActualDegreeOpen( howOpen );
			}
		};

		private View.OnClickListener toggleContentListener = new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				toggle( accordion.getOpenTransitionEnabled() );
			}
		};

	}

}
