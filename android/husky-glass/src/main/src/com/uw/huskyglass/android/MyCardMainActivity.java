package com.uw.huskyglass.android;

import com.uw.huskyglass.android.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

@SuppressWarnings("unused")
public class MyCardMainActivity extends Activity {

	private CardUI mCardView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_main);

		// initialize CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		// add AndroidViews Cards
		/*mCardView.addCard(new MyCard("Get the CardsUI view"));
		mCardView.addCardToLastStack(new MyCard("for Android at"));
		MyCard androidViewsCard = new MyCard("www.androidviews.net");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.androidviews.net/"));
				startActivity(intent);

			}
		});
		mCardView.addCardToLastStack(androidViewsCard);

		// add one card, and then add another one to the last stack.
		mCardView.addCard(new MyCard("2 cards"));
		mCardView.addCardToLastStack(new MyCard("2 cards")); */

		// add one card
		MyImageCard husky1 = new MyImageCard("Husky Card 1",R.drawable.card1);
		// MyImageCard husky2 = new MyImageCard("Husky Card 2",R.drawable.card2);
		// MyImageCard husky3 = new MyImageCard("Husky Card 3", R.drawable.card3);
		
		mCardView.addCardToLastStack(husky1);
		// mCardView.addCardToLastStack(husky2);
		// mCardView.addCardToLastStack(husky3);
		
		husky1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setData(Uri.parse("http://www.androidviews.net/"));
				// startActivity(intent);
				
				MainActivity mainActivity = new MainActivity();
				mainActivity.createNewTimelineItem();

			}
		});
		
		/* mCardView.addCard(new MyImageCard("Husky Card 1",R.drawable.card1));
		mCardView.addCardToLastStack(new MyImageCard("Husky Card 2",R.drawable.card2));
		mCardView.addCardToLastStack(new MyImageCard("Husky Card 3", R.drawable.card3)); */


		// create a stack
		/* CardStack stack = new CardStack();
		stack.setTitle("title test");

		// add 3 cards to stack
		stack.add(new MyCard("3 cards"));
		stack.add(new MyCard("3 cards"));
		stack.add(new MyCard("3 cards"));

		// add stack to cardView
		mCardView.addStack(stack); */

		// draw cards
		mCardView.refresh();
	}
}
