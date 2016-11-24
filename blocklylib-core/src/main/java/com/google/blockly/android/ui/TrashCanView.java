/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.blockly.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.R;
import com.google.blockly.android.control.BlocklyController;

/**
 * Drop target view for deleting blocks via a drag gesture, with animation.
 * <p/>
 * This view has two layout attributes, {@code closedIcon} and {@code openedIcon}. Each is a
 * reference to a drawable resource for one of the two trash states, closed (default/idle state) and
 * opened (pending drop during drag hover).
 */
public class TrashCanView extends FrameLayout {
    protected ImageView mDefaultView;
    protected ImageView mOnHoverView;

    public TrashCanView(Context context) {
        this(context, null, 0);
    }

    public TrashCanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrashCanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildUI();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TrashCanView,
                0, 0);
        try {
            //noinspection ResourceType
            setDefaultIcon(a.getResourceId(
                    R.styleable.TrashCanView_defaultIcon, R.drawable.blockly_trash));
            setOnHoverIcon(a.getResourceId(
                    R.styleable.TrashCanView_onHoverIcon, R.drawable.blockly_trash_open));
        } finally {
            a.recycle();
        }
    }

    /**
     *
     * @param controller
     */
    public void setBlocklyController(BlocklyController controller) {
        if (controller == null) {
            setOnDragListener(null);
            return;
        }

        setOnDragListener(new OnDragToTrashListener(controller) {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (isTrashableBlock(event)) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            mOnHoverView.setVisibility(View.VISIBLE);
                            mDefaultView.setVisibility(View.INVISIBLE);
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                        case DragEvent.ACTION_DROP:
                        case DragEvent.ACTION_DRAG_ENDED:
                            mOnHoverView.setVisibility(View.INVISIBLE);
                            mDefaultView.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                return super.onDrag(v, event);  // Defer to OnDragToTrashListener
            }
        });
    }

    public void setDefaultIcon(int drawableRes) {
        setDefaultIcon(ContextCompat.getDrawable(getContext(), drawableRes));
    }

    public void setDefaultIcon(Drawable drawable) {
        mDefaultView.setImageDrawable(drawable);
    }

    /**
     * Set the drawable to show when dragging a block above the
     * @param drawableRes
     */
    public void setOnHoverIcon(int drawableRes) {
        setOnHoverIcon(ContextCompat.getDrawable(getContext(), drawableRes));
    }

    public void setOnHoverIcon(Drawable drawable) {
        mOnHoverView.setImageDrawable(drawable);
    }

    private void buildUI() {
        setMeasureAllChildren(true);

        mDefaultView = new ImageView(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mDefaultView, lp);

        mOnHoverView = new ImageView(getContext());
        mOnHoverView.setVisibility(View.INVISIBLE);
        lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mOnHoverView, lp);

        final Context context = getContext();
        if (context instanceof AbstractBlocklyActivity) {
            // If this view was inflated, the BlocklyController may not be ready quite yet.
            post(new Runnable() {
                @Override
                public void run() {
                    setBlocklyController(((AbstractBlocklyActivity) context).getController());
                }
            });
        }
    }
}
