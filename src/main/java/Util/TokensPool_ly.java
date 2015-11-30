package Util;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by 叶 on 2015/5/19.
 * <p/>
 * tokenList : 存放获取到的一系列token
 * pos : 存储当前token的位置
 * fPath : 将tokenList写入文件，fpath是文件路径
 * <p/>
 * 将获取到的token存入tokenList中，管理tokenList，进行定期更换
 */
public class TokensPool_ly {
    int pos = 0;
    ArrayList<Token_ly> tokenList = null;
    String fPath = "." + File.separator + "Tokens" + File.separator + "tokens.txt";

    public TokensPool_ly() {
        mainFunction();
    }

    public TokensPool_ly(String path) {
        setFPath(path);
        mainFunction();
    }

    public void setFPath(String fPath) {
        this.fPath = fPath;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public ArrayList<Token_ly> getTokenList() {
        return tokenList;
    }

    //重置位置
    public void ResetPos() {
        pos = 0;
    }

    //判断是否读取到tokenList的最后一个元素
    public boolean IsOver() {
        return (pos >= tokenList.size());
    }

    public void mainFunction() {
        tokenList = new ArrayList<Token_ly>();
        String inline = "";
        String path = fPath;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            int c = 0;
            while ((inline = reader.readLine()) != null) {
                if (inline.equals(""))
                    continue;
                tokenList.add(new Token_ly(inline));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
