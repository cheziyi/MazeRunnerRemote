package ntu.cz3004.mazerunnerremote.managers;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by Aung on 3/7/2018.
 */

public class GuiUtilManager {

    public static int[] colorIdToHexLong(Context context, int dimColorId, int highlightedColorId, float positionOffset){

        long dimColor = ContextCompat.getColor(context, dimColorId);
        long highlightedColor = ContextCompat.getColor(context, highlightedColorId);

        int iA = (int) ((dimColor >> 24) & 0xFF);
        int iR = (int) ((dimColor >> 16) & 0xFF);
        int iG = (int) ((dimColor >> 8) & 0xFF);
        int iB = (int) ((dimColor) & 0xFF);

        int fA = (int) ((highlightedColor >> 24) & 0xFF);
        int fR = (int) ((highlightedColor >> 16) & 0xFF);
        int fG = (int) ((highlightedColor >> 8) & 0xFF);
        int fB = (int) ((highlightedColor) & 0xFF);


        int mA = Math.round(iA + (fA - iA) * positionOffset);
        int mR = Math.round(iR + (fR - iR) * positionOffset);
        int mG = Math.round(iG + (fG - iG) * positionOffset);
        int mB = Math.round(iB + (fB - iB) * positionOffset);

        int nA = Math.round(fA + (iA - fA) * positionOffset);
        int nR = Math.round(fR + (iR - fR) * positionOffset);
        int nG = Math.round(fG + (iG - fG) * positionOffset);
        int nB = Math.round(fB + (iB - fB) * positionOffset);


        String hex1 = String.format("%02x%02x%02x%02x", mA,mR, mG, mB);
        String hex2 = String.format("%02x%02x%02x%02x", nA,nR, nG, nB);

        int value1 = (int) Long.parseLong(hex1, 16);
        int value2 = (int) Long.parseLong(hex2, 16);

        return new int[]{value1, value2};

    }
}
