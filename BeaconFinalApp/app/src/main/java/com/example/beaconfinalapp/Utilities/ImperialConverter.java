package com.example.beaconfinalapp.Utilities;

public class ImperialConverter {


    double RESULT;
    public double Converter(boolean SET, int DIS){// converter method

        if(SET = true) {
            RESULT = Math.round((DIS / 1.609) * 100.0) / 100.0;
        } else{
            RESULT = Math.round((DIS * 1.609) * 100.0) / 100.0;
        }
        return RESULT;

    }
    //TODO: Create Convert on load method

}
