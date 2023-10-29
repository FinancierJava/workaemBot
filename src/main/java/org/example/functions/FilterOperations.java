package org.example.functions;

import org.example.commands.AppBotCommand;

import java.util.Random;

public class FilterOperations {
    @AppBotCommand(name = "blackWhite", description = "blackWhite filter", showInKeyBoard = true)
    public static float[] blackWhite(float[] rgb) {
        float BW = (rgb[0] + rgb[1] + rgb[2]) / 3;
        if (BW < 0.5) {
            rgb[0] = rgb[1] = rgb[2] = 0;
        }
        else {
            rgb[0] = rgb[1] = rgb[2] = 1;
        }
        return rgb;
    }

    @AppBotCommand(name = "whiteBlack", description = "whiteBlack filter", showInKeyBoard = true)
    public static float[] whiteBlack(float[] rgb) {
        float WB = (rgb[0] + rgb[1] + rgb[2]) / 3;
        if (WB < 0.5) {
            rgb[0] = rgb[1] = rgb[2] = 1;
        }
        else {
            rgb[0] = rgb[1] = rgb[2] = 0;
        }
        return rgb;
    }

    @AppBotCommand(name = "negative", description = "negative filter", showInKeyBoard = true)
    public static float[] negative(float[] rgb) {
        rgb[0] = 1 - rgb[0];
        rgb[1] = 1 - rgb[1];
        rgb[2] = 1 - rgb[2];
        return rgb;
    }

    @AppBotCommand(name = "noiseUP", description = "noiseUP filter", showInKeyBoard = true)
    public static float[] noiseUP(float[] rgb) {
        Random random = new Random();
        float delta = random.nextFloat(0.3f);
        rgb[0] = (rgb[0] + delta) > 1 ? rgb[0] : (rgb[0] + delta);
        rgb[1] = (rgb[1] + delta) > 1 ? rgb[1] : (rgb[1] + delta);
        rgb[2] = (rgb[2] + delta) > 1 ? rgb[2] : (rgb[2] + delta);
        return rgb;
    }

    @AppBotCommand(name = "noiseDown", description = "noiseDown filter", showInKeyBoard = true)
    public static float[] noiseDown(float[] rgb) {
        Random random = new Random();
        float delta = random.nextFloat(0.3f);
        rgb[0] = (rgb[0] - delta) < 0 ? rgb[0] : (rgb[0] - delta);
        rgb[1] = (rgb[1] - delta) < 0 ? rgb[1] : (rgb[1] - delta);
        rgb[2] = (rgb[2] - delta) < 0 ? rgb[2] : (rgb[2] - delta);
        return rgb;
    }

    @AppBotCommand(name = "sepia", description = "sepia filter", showInKeyBoard = true)
    public static float[] sepia(float[] rgb) {
        Random random = new Random();
        float delta = random.nextFloat(0.3f);
        rgb[0] = (rgb[0] + delta) > 1 ? 1 : (rgb[0] + delta);
        rgb[1] = (rgb[1] + delta / 2) > 1 ? 1 : (rgb[1] + delta / 2);
        return rgb;
    }

    @AppBotCommand(name = "onlyYellow", description = "onlyYellow filter", showInKeyBoard = true)
    public static float[] onlyYellow(float[] rgb) {
        rgb[2] = 0;
        return rgb;
    }

    @AppBotCommand(name = "onlyViolet", description = "onlyViolet filter", showInKeyBoard = true)
    public static float[] onlyViolet(float[] rgb) {
        rgb[1] = 0;
        return rgb;
    }

    @AppBotCommand(name = "onlyTurquoise", description = "onlyTurquoise filter", showInKeyBoard = true)
    public static float[] onlyTurquoise(float[] rgb) {
        rgb[0] = 0;
        return rgb;
    }

    @AppBotCommand(name = "grayScale", description = "grayScale filter", showInKeyBoard = true)
    public static float[] grayScale(float[] rgb) {
        final float mean = (rgb[0] + rgb[1] + rgb[2]) / 3;
        rgb[0] = mean;
        rgb[1] = mean;
        rgb[2] = mean;
        return rgb;
    }
    @AppBotCommand(name = "onlyRed", description = "onlyRed filter", showInKeyBoard = true)
    public static float[] onlyRed(float[] rgb) {
        rgb[1] = 0;
        rgb[2] = 0;
        return rgb;
    }

    @AppBotCommand(name = "onlyGreen", description = "onlyGreen filter", showInKeyBoard = true)
    public static float[] onlyGreen(float[] rgb) {
        rgb[0] = 0;
        rgb[2] = 0;
        return rgb;
    }

    @AppBotCommand(name = "onlyBlue", description = "onlyBlue filter", showInKeyBoard = true)
    public static float[] onlyBlue(float[] rgb) {
        rgb[0] = 0;
        rgb[1] = 0;
        return rgb;
    }
    @AppBotCommand(name = "brightness", description = "Adjust brightness", showInKeyBoard = true)
    public static float[] adjustBrightness(float[] rgb, float factor) {
        rgb[0] = Math.min(Math.max(rgb[0] * factor, 0), 1);
        rgb[1] = Math.min(Math.max(rgb[1] * factor, 0), 1);
        rgb[2] = Math.min(Math.max(rgb[2] * factor, 0), 1);
        return rgb;
    }

    @AppBotCommand(name = "contrast", description = "Adjust contrast", showInKeyBoard = true)
    public static float[] adjustContrast(float[] rgb, float factor) {
        rgb[0] = Math.min(Math.max((rgb[0] - 0.5f) * factor + 0.5f, 0), 1);
        rgb[1] = Math.min(Math.max((rgb[1] - 0.5f) * factor + 0.5f, 0), 1);
        rgb[2] = Math.min(Math.max((rgb[2] - 0.5f) * factor + 0.5f, 0), 1);
        return rgb;
    }

    @AppBotCommand(name = "invertColors", description = "Invert colors", showInKeyBoard = true)
    public static float[] invertColors(float[] rgb) {
        rgb[0] = 1 - rgb[0];
        rgb[1] = 1 - rgb[1];
        rgb[2] = 1 - rgb[2];
        return rgb;
    }
}
