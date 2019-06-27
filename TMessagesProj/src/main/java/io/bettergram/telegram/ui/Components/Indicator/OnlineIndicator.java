package io.bettergram.telegram.ui.Components.Indicator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import io.bettergram.telegram.messenger.AndroidUtilities;
import io.bettergram.telegram.ui.ActionBar.Theme;

public class OnlineIndicator {

    private final int borderWidth = AndroidUtilities.dp(2);

    private Paint paint = Theme.dialog_activeStatePaint;

    private int offsetX, offsetY;
    private int radius;

    private long dialog_id;

    private int active = -1;

    public OnlineIndicator() {
    }

    public OnlineIndicator dialog(long dialog_id) {
        this.dialog_id = dialog_id;
        return this;
    }

    public OnlineIndicator offsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public OnlineIndicator offsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public OnlineIndicator anchorAvatarRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public OnlineIndicator active(int active) {
        this.active = active;
        return this;
    }

    public void draw(Canvas canvas) {
        if (dialog_id == 0) {
            return;
        }

        if ((dialog_id >> 32) == 0 && active != -1) {
            float x0 = radius;
            float y0 = radius;
            float dx = (float) (x0 + radius * Math.cos(40 * Math.PI / 180));
            float dy = (float) (y0 + radius * Math.sin(40 * Math.PI / 180));
            float circleSize = radius * 0.25f;
            paint.setColor(Theme.getColor(Theme.key_dialog_activeStateBorderColor));
            canvas.drawCircle(offsetX + dx, offsetY + dy, circleSize + borderWidth, paint);
            paint.setColor(Theme.getColor(active == 1 ? Theme.key_dialog_activeStateOnlineColor : Theme.key_dialog_activeStateOfflineColor));
            canvas.drawCircle(offsetX + dx, offsetY + dy, circleSize, paint);
        }
    }


}
