package GetStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.Response;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;


import Util.MyLogger;
import Util.ShutdownThread;
import Util.StringReaderOne;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import Util.myWriter;

public class GetRepost {

String thistimedir = null;
    
    static final String repostdir = "."+File.separator+"data"+File.separator+"repost"+File.separator;/*repostmid文件夹路径*/
    //static final String uiddir = ".\\realestate\\uid\\";/*uid文件夹路径*/

    String midpath = "."+File.separator+"data"+File.separator+"mid_needGet";
    protected String completeuidPath = null;
    
    String completemidPath = "."+File.separator+"data"+File.separator+"complete_mid";
    static StringReaderOne readmid = null;;
    static StringReaderOne readuid = null;;
    protected String outputDir = null;

    TokenManage tm = null;
    Token tokenpack = null;
    Weibo weibo = null;
    String start = null;
    SimpleDateFormat inputFormat = null;;

    LogManager lMgr = LogManager.getLogManager();
    String thisName = "WeiboLog";
    Logger log = Logger.getLogger(thisName);
    MyLogger mylogger = null;

    Set<String> listSet = null;
    Set<String> completeMidSet = null;

    int pagecount = 200;
    long startTime = 0l;
    Set<String> failedToken = new HashSet<String>();
    long nowStatusCount = 0;
    int hisStatusCount = 0;
    int getStatusCount = 0;
    int day = 1; 
    void init()
    {
        inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        startTime = new Date().getTime();
        mylogger = new MyLogger(inputFormat.format(startTime));
        lMgr.addLogger(log);
        readuid = new StringReaderOne(midpath);
        this.outputDir = "."+File.separator+"data"+File.separator+"repost"+File.separator;
        this.completeuidPath = "." + File.separator + "data" + File.separator+ "complete_mid";
        listSet = new HashSet<String>();
        completeMidSet = new HashSet<String>();
        try
        {
            Tool.refreshToken();
            tm = new TokenManage();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        System.out.println("Start Crawling at " + inputFormat.format(startTime));

        thistimedir = thistimedir + File.separator;
        readCompleteMid();
    }

    public void newFolder(String folderPath)
    {
        try
        {
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            if (!myFilePath.exists())
            {
                myFilePath.mkdir();
            }
        }
        catch (Exception e)
        {
            System.out.println("新建文件夹操作出错");
            e.printStackTrace();
        }
    }

    int MutedTest(Weibo weibo, String id)
    {
        int count = 0;
        return count;
    }

    void deconstruct()
    {
        tm.CloseLogger();
        System.out.println("All data get succeeded at "
                + inputFormat.format(new Date().getTime()));
    }

    public int GetRepostByMid(Weibo weibo, String mid, myWriter writeStatus, int page)
    {
        List<Status> statusList = new ArrayList<Status>();
        StatusWapper wapper = null;
        int ret = 0;
        //StatusWapper res;
        Response res = null;
        Paging paging = new Paging();
        paging.setCount(200);
        paging.setPage(page);
        System.out.println("page : "+ page);
        while(true)
        {
            try
            {
                res = weibo.getRepostTimeline1(mid,paging);
                //判断是否爬取正确格式的文件
                if(!res.toString().equals("[]") && res!=null)
                {
                    wapper = Status.constructWapperStatus(res.toString());
                    if(wapper == null)
                    {
                        continue;
                    }
                    statusList = wapper.getStatuses();
                }else
                {
                    return 0;
                }
                
                //将结果写回文件
                if (res.toString().length() > 0 && statusList.size()>0)
                {
                    writeStatus.Write(res.toString());
                }
                break;
            } catch (weibo4j.model.WeiboException e1) {
                
                //e2.printStackTrace();
            } catch (WeiboException e) {
                System.out.println("error");
                // TODO Auto-generated catch block
                // TODO Auto-generated catch block
                if(e.getStatusCode() == 400 || e.getStatusCode() == 401)
                {
                    
                    System.out.println("token invalid, change token");
                    //failedToken.add(tokenpack.token);
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, mid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                    {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }
                    weibo.setToken(tokenpack.token);
                }else if(e.getStatusCode() == 403)
                {
                    System.out.println("error1");
                    tm.ChekState();
                    tokenpack = tm.GetToken();
                    tm.AddIPCount(1, mid);
                    while (tokenpack == null || failedToken.contains(tokenpack.token))
                    {
                        System.out.println(tm.GetToken());
                        tokenpack = tm.GetToken();
                    }  
                    weibo.setToken(tokenpack.token);
                    System.out.println("request too many times , sleep 5~45s");
                    System.out.println(e.getMessage());
                    try
                    {
                        double a = Math.random()*50000;  
                        a = Math.ceil(a);  
                        int randomNum = new Double(a).intValue(); 
                        System.out.println("sleep : " +randomNum/1000 +"s");
                        Thread.sleep(randomNum);
                    }catch (InterruptedException e1)
                    {
                        //e1.printStackTrace();
                    }
                    
                    
                }
            }
        }
        try
        {
            double a = Math.random()*5000;  
            a = Math.ceil(a);  
            int randomNum = new Double(a).intValue(); 
            System.out.println("sleep : " +randomNum/1000 +"s");
            Thread.sleep(randomNum);
        }
        catch (InterruptedException e1)
        {
            //e1.printStackTrace();
        }
        if (statusList.size() > 0)
        {
            ret = statusList.size();
            System.out.println("Succeded");
        }
        statusList.clear();
        System.out.println("ret : " + ret);
        return ret;
    }

    void WriteFailLog()
    {
        System.out.println(inputFormat.format(new Date())
                + " #Fail Get id at rootuid pos of " + readmid.getPos());
        mylogger.Write(inputFormat.format(new Date())
                + " #Fail Get id at rootuid pos of " + readmid.getPos());
        System.out.println(inputFormat.format(new Date())
                + " #Fail Get Token at token pos " + tm.getPos());
        mylogger.Write(inputFormat.format(new Date())
                + " #Fail Get Token at token pos " + tm.getPos());
    }

    private static boolean IsConnectDisableOfNCEU(String SC)
    {
        Pattern pattern=Pattern.compile("window\\.location='http://202\\.120\\.95\\.235'");
        Matcher matcher=pattern.matcher(SC);
        if(matcher.find())
        {
            return true;
        }
        return false;
    }
    void readCompleteMid()
    {
        File file = new File(completemidPath);
        if(!file.exists())
        {
            return;
        }
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf-8");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                if (!completeMidSet.contains(line))
                {
                    completeMidSet.add(line);
                }
            }
            fis.close();
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    void CrawlRepost()
    {
         myWriter writeStatus = null;
        while (!readuid.IsOver())
        {
           //每12小时更新一次token
            if(new Date().getTime() > startTime+day*12*60*60*1000)
            {
                ++day;
                //从网络上更新token
                //Tool.refreshToken();
                //更新代码使用token
                tm = new TokenManage();
            }
            String mid = String.valueOf(readuid.GetStrictNewID());
            
            //recovery
            if(completeMidSet.contains(mid))
            {
                System.out.println(mid + " has been crawled");
                readuid.idOK();
                continue;
            }
            nowStatusCount = 0;
            
            writeStatus = new myWriter(this.outputDir+mid+"_repost", false);
            
            System.out.println("Start getting " + mid);
            getStatusCount = 0;
            for(int page=1;;page++)
            {
                tm.ChekState();
                tokenpack = tm.GetToken();

                while(tokenpack == null)
                {
                    tokenpack = tm.GetToken();
                }
                weibo = new Weibo();
                weibo.setToken(tokenpack.token);

                // -----------------------------------------------------------------
                int responsecount = 0;
                responsecount = GetRepostByMid(weibo,mid,writeStatus,page);
                

                // -----------------------------------------------------------------

                tm.AddIPCount(1, mid.toString());
                System.out.println("ipcount: " + TokenManage.getIpcount());
                if ((responsecount <= 0))
                {
                    break;
                }
                
            }
            writeStatus.closeWrite();
            Tool.write(completeuidPath,mid);
            readuid.idOK();
        }
        System.out.println("=========completed==============");
    }

    private void write(String filepath,String str)
    {
        FileWriter fileWriter = null;
        BufferedWriter bw= null;
        try {
            fileWriter = new FileWriter(filepath,true);
            bw = new BufferedWriter(fileWriter);
            bw.append(str);
            bw.newLine();    
            bw.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    // ---------------------------------------------------------------------------------------------
    public void run()
    {
        init();

        CrawlRepost();

        // CrawlUid();

        deconstruct();
    }

    public static void main(String[] args)
    {
        ShutdownThread shutdown = new ShutdownThread();

        GetRepost randu = new GetRepost();
        randu.run();
    }
}
