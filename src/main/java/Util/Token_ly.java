package Util;

import java.util.Date;

/**
 * Created by 叶 on 2015/5/19.
 *
 */
public class Token_ly implements Comparable<Token_ly>{

    public String token;
    int count;
    long timestamp;

    /*oauth2.0*/
    Token_ly(String at)
    {
        token = at;
        count = 0;
        timestamp = (new Date()).getTime();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void addCount(){
        this.count ++;
    }

	public int compareTo(Token_ly arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

    
}
