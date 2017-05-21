package com.accumulation.lib.configuration.base;

import com.accumulation.lib.configuration.core.Action;
import com.accumulation.lib.configuration.core.Screen;
import com.accumulation.lib.configuration.core.ViewInflater;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ScreenDialog extends DialogFragment {
	public static ScreenDialog createDialog(Screen data, Action action) {
		ScreenDialog dlg = new ScreenDialog();
		Bundle args = new Bundle();
		args.putSerializable("screen", data);
		args.putSerializable("action", action);
		dlg.setArguments(args);
		return dlg;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Screen screen = (Screen) getArguments().getSerializable("screen");
		Action action = (Action) getArguments().getSerializable("action");
		Dialog dlg = new Dialog(getActivity());
		dlg.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dlg.setContentView(ViewInflater.inflateView(getActivity(), null, screen.getView(),
				action.getBind()));
		return dlg;
	}

}
