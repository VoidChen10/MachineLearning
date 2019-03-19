import java.io.*;

/**
 * @Author: voidChen
 * @Date: 2019/3/15 10:17
 * @Version 1.0
 */
public class MusicUtil {

    public static void main(String[] args){
        try{
            File inFile = new File("E:\\音乐\\VipSongsDownload\\华晨宇 - 山海 (Live).qmcflac");
            File outFile = new File("E:\\音乐\\VipSongsDownload\\华晨宇 - 山海 (Live).flac");

            DataInputStream dis = new DataInputStream( new FileInputStream(inFile));
            DataOutputStream dos = new DataOutputStream( new FileOutputStream(outFile));
            byte[] by = new byte[1000];
            int len;
            while((len=dis.read(by))!=-1){
                for(int i=0;i<len;i++){
                    by[i]^=0xa3;
                }
                dos.write(by,0,len);
            }
            dis.close();
            dos.close();
        }catch(IOException ioe){
            System.err.println(ioe);
        }
    }

}
