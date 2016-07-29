package com.guarantime;

import java.util.List;

/**
 * Created by bilal on 2/24/2016.
 */
public class Free_slot {
    private String start_date;
    private String end_date;
    public Free_slot(String start_date, String end_date){
        this.start_date=start_date;
        this.end_date=end_date;
    }
    public String getStart_date(){
        return start_date;
    }
    public String getEnd_date(){
        return end_date;
    }
}
