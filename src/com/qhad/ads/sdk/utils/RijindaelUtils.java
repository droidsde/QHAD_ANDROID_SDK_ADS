package com.qhad.ads.sdk.utils;

import com.qhad.ads.sdk.logs.QHADLog;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Duan
 */
public final class RijindaelUtils {

    //密钥库
    public static String[] KEY_STORE = new String[]{
            "57364A78387668595352416F7530426C48747470516C6F6443634A6961454434",
            "4A7634414F704953583567506D75447A6B5A7262697165594769396336525650",
            "30594A6378374A345243455A3039355A4739486C4E5433623136574637506736",
            "516D6B7048363137726C6472757A754776537357334E334145667A765741514C",
            "764252624449656F3876506B76593270366841647A4F6E424B6F306879393731",
            "6E53643066347563684476436A514D48564C334C734D777473724D473047486F",
            "6C5542304835555448787063785672485237374E33367067783753594A683032",
            "38655979314F6E50494F767963443762587556354567566F573645636C546472",
            "373243386B7077303230444A664E4F444A685253316C4D64614174764C537452",
            "304B46615047565269735833664D763259567141394869743174465A71665A6F",
            "376A6852676F484A30724E50756F31504E4259456576496F434F306C34716561",
            "684D4B4150386654366136374848687353634858497145493879726658583764",
            "7037347834576A7A464B5361444B464F546D6A4268744E335630614944636332",
            "667177694141736F55546D5279384E7645374D625A4F69506963463063704F71",
            "776E4B5533705531665638374E4761787A536774684C6D664D4B623975655659",
            "6B4644666C3075484B53555142473073534176537066685A716A455274566B4D",
            "41497A496B6E61424B393363704355456F7049446D703149486B52336D665857",
            "5656575051503470716670756C4F45544965423345695A626658707358336651",
            "54716D4B56614271454B3278525231565A5438744B4C31306C6C65656E6B6367",
            "6F5254656364714D6D4A5248636B7452693141636F663130535A524542675359",
            "6A6158757456526143694E334869706835437559746E67684450455066683379",
            "764955524E55364842505538636F78654A4A30634E6850335073397279323631",
            "544F416E6230623147334872497855506349586D714F7741656B4F52614A3732",
            "465145655549314E33315A6935497361754A43514962694B4C5354733647414C",
            "70395769426B3273306B684464747476476D777A48386C326842516E32555873",
            "704737677A636865347A755037795339665A54444C306777574C374B69436A48",
            "7547644D44356430553463366730344D59384C54426B675A7132335434796C79",
            "507674504D436D7231445A5448537267366C7072774C5431646C79306E507562",
            "337A42545874625071746C5647396F416A767142306175305337463851476853",
            "72486275716F75666158346C41516B363067333532556E6B5374647535633076",
            "3968514C6A76556C69454D763551714D7370775272526B6C44544E70464A524F",
            "6F57345538444C55617342364A4D416258634B683046306B4F524B5033374870",
            "71486B3041344F5367694B394A6C72366E4D63624A48494C495052506A73474A",
            "44566767624F7A7748315A525935454768345A487547413879365141566E5079",
            "6739793154376259657451687748334144794F6A67656E357161433968327679",
            "784A4D6D754C4C323136445962744D4E464D6A5A37624F3266357951576F5874",
            "4A6C383251596242355A3031673752577372737939674B45416B6C6B3451554C",
            "4349443167426D5241427432734646633976576E396372557058646E4B6E516D",
            "7855724D6B45354B5666665A476955334277676E75544F454F5172646667754D",
            "6B5651424253396B3858644A5A70596F3748786B5478656A6A584558704D4F4A",
            "6E6E7174776B6C4D504F43376E6B4573526E6B786175627970743563325A4471",
            "4A4458496C42574A777A4F465946756C735930496F74754272464569336A584E",
            "61667549736B6E726A3856444C4F656C5562526845496E7778583151695A7372",
            "3761625549696F48626266367834635468336F654A544A6C51746C7741567849",
            "5256433968396872654F487642385438315052446350716358433058494C717A",
            "46524C47566751526F4A50335734517636396435597771707655497070734934",
            "6F334A4D79375A454E56774E4E766A58764870486F6379765343664A376D3476",
            "6B446A58566459654278306730793831664D686E6430306843664D37476C6A30",
            "6143494E347A4A7555534C53785159554B76635246536D576E6A6B746B473375",
            "754348347033757248696E353776536E454552484F3675794E63314D61523146",
            "6D786D7971303969474E6651504C4C61634B3678706C39394B6C45444F453838",
            "655066517474386758445451584E493950623437584F7771663153776D744641",
            "305169723744624B6F626D57456C44754C4D487A32524C4B6541593743367A42",
            "4E5174794A314B564F344B65747278356F6E6A316F75447633684E4B686A6534",
            "43696570694C6E62594A434E514C734F4C67537A68577364387564706B656358",
            "63473839386D434139313154515A38735A6C33674649537158384C4334584168",
            "5364684F584850466E764C6F434177534A4E78543330644B59494A5A51425349",
            "596276566256644A4E303064736333506F6C42336F63424D6A335478706F596E",
            "3036564231666C3065704F6348424955744E693930486D4A6346694553413251",
            "6D575441525258375A4E686E6851693747624A6678524152397A696F48485433",
            "514263584A714D35367934547A4750394B6336774255744C525933744C685542",
            "6678696A4E59757635323436566F6E7A5463586C55566B6A4A334E7A746D4B4A",
            "55495A6B52465567315349476641354747576C7832484F585859677A71797269",
            "4A643659336F304834567855535261755330615479674D6B6943686C4A586873"
    };

    /**
     * 加密算法
     *
     * @param content：明文
     * @param password：密码
     * @return
     */
    public static String encrypt(String content, String password) {
        if (isEmpty(content) || isEmpty(password)) {
            return null;
        }

        try {
            ArrayList<byte[]> ptList = new ArrayList<byte[]>();//明文的byte字节块列表
            ArrayList<byte[]> ctList = new ArrayList<byte[]>();//密文的byte字节块列表

            //拿到明文之后进行分块(每16个字节为一块)，不足16个字节的块使用随机数填充
            byte[] contentBytes = null;
            int len = content.getBytes().length;//明文的字节长度(非中文的)
            int mod = len % 16;

            if (mod != 0) {
                Random random = new Random();
                String sub = "";
                for (int i = 0; i < 16 - mod; i++) {
                    sub += (char) (random.nextInt(10) + 48) + "";//转成ASCII码
                }
                content = content + sub;
            }
            contentBytes = content.getBytes();//获取到明文的字节

            /**将上面操作完的明文字节数组进行分块，并且存到列表中**/
            int block = contentBytes.length / 16;
            for (int i = 0; i < block; i++) {
                byte[] temp = new byte[16];
                int k = 0;
                for (int j = i * 16; j < i * 16 + 16; j++) {
                    temp[k] = contentBytes[j];
                    k++;
                }
                ptList.add(temp);
            }

            /**对每一个块进行加密，加密之后的内容存到ctList列表中*/
            for (int i = 0; i < ptList.size(); i++) {
                ctList.add(encrypt(password, ptList.get(i)));
            }

            String lenStr = String.valueOf(len);
            StringBuilder sbs = new StringBuilder();
            for (int i = 0; i < lenStr.length(); i++) {
                sbs.append('0');
                sbs.append(lenStr.charAt(i));
            }

            byte[] failByte = new byte[16];
            String failStr = "00000000000000000000000000000000";
            //比如:长度为12映射为:00000000000...0012,共16个字节
            failStr = failStr.substring(0, (failStr.length() - sbs.toString().length())) + sbs.toString();
            failByte = hexStr2Bytes(failStr);
            for (int i = 0; i < failByte.length; i++) {
                failByte[i] += 48;
            }

            //将明文的长度的字节数据进行加密存到ctList的尾部
            ctList.add(encrypt(password, failByte));

            /**将密文转化成十六进制字符串*/
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ctList.size(); i++) {
                sb.append(byte2HexStr(ctList.get(i)));
                sb.append("");
            }

            return sb.toString().trim();
        } catch (Exception e) {
            QHADLog.d("加密失败");
        }

        return null;
    }

    /**
     * 解密算法
     *
     * @param content：解密内容
     * @param password:解密密码
     * @return
     */
    public static String decrypt(String content, String password) throws Exception {

        //如果解密内容和解密密码无效就不进行解密，直接返回null
        if (isEmpty(content) || isEmpty(password)) {
            return null;
        }

        //将解密的内容转成字节数组(因为加密之后的是十六进制的字符串)
        byte[] contentByte = hexStr2Bytes(content);
        //得到密文的块数
        int block = contentByte.length / 16;

        ArrayList<byte[]> ct = new ArrayList<byte[]>();//密文的Bytes块列表
        ArrayList<byte[]> pt = new ArrayList<byte[]>();//明文的Bytes块列表

        /**将密文进行分块，并且存到ct列表中**/
        for (int i = 0; i < block; i++) {
            byte[] temp = new byte[16];
            for (int j = 0; j < 16; j++) {
                temp[j] = contentByte[i * 16 + j];
            }
            ct.add(temp);
            temp = null;
        }

        /**将密文块进行解密，然后存到pt列表中**/
        for (int i = 0; i < ct.size(); i++) {
            pt.add(decrypt(password, ct.get(i)));
        }

        //得到明文的长度
        for (int i = 0; i < pt.get(pt.size() - 1).length; i++) {
            pt.get(pt.size() - 1)[i] -= 48;
        }

        StringBuilder lenStr = new StringBuilder();
        byte[] lenByte = pt.get(pt.size() - 1);
        for (int i = 0; i < lenByte.length; i++) {
            lenStr.append(String.valueOf(lenByte[i]));
        }

        System.out.println("Len:" + Integer.valueOf(lenStr.toString()));
        int lens = Integer.valueOf(lenStr.toString());

        /**取得有效的明文长度数据*/
        byte[] contentBytes = new byte[pt.size() * 16];
        int location = 0;
        for (int i = 0; i < pt.size(); i++) {
            byte[] temp = pt.get(i);
            for (int j = 0; j < temp.length; j++) {
                contentBytes[location] = temp[j];
                location++;
            }
            temp = null;
        }
        return new String(contentBytes, 0, lens);
    }

    /**
     * Rijindael的加密算法
     *
     * @param passsword：加密密码
     * @param content:加密内容的byte数组
     * @return
     */
    private static byte[] encrypt(String passsword, byte[] content) throws Exception {
        Rijndael rijndael = new Rijndael();
        rijndael.makeKey(passsword.getBytes(), 256);//采用128的加密密钥
        byte[] ct = new byte[16];//对16bytes进行加密
        rijndael.encrypt(content, ct);
        return ct;
    }

    /**
     * Rijindael的解密算法
     *
     * @param passsword：解密密码
     * @param content：解密内容的byte数组
     * @return
     */
    private static byte[] decrypt(String passsword, byte[] content) throws Exception {
        Rijndael rijndael = new Rijndael();
        rijndael.makeKey(passsword.getBytes(), 256);
        byte[] ct = new byte[16];
        rijndael.decrypt(content, ct);
        return ct;
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param byte[] b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append("");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * int类型转化成byte数组
     *
     * @param res
     * @return
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);//最低位
        targets[1] = (byte) ((res >> 8) & 0xff);//次低位
        targets[2] = (byte) ((res >> 16) & 0xff);//次高位
        targets[3] = (byte) (res >>> 24);//最高位,无符号右移。
        return targets;
    }

    /**
     * byte数组转成int类型
     *
     * @param res
     * @return
     */
    public static int byte2int(byte[] res) {
        int targets = 0;
        targets += res[3];
        targets += res[2] * (2 << 8);
        targets += res[1] * (2 << 16);
        targets += res[0] * (2 << 24);
        return targets;
    }

    /**
     * 将64位的十六进制的密钥库转成32位的字符串密钥
     *
     * @param hexStr
     * @return
     */
    public static String hexPasswordToStrPassword(String hexStr) {
        byte[] bytes = RijindaelUtils.hexStr2Bytes(hexStr);
        char[] charAry = new char[32];
        for (int i = 0; i < bytes.length; i++) {
            char a = (char) (bytes[i]);
            charAry[i] = a;
        }
        return String.valueOf(charAry);
    }

    /**
     * 判断字符串不为空
     *
     * @param content
     * @return
     */
    public static boolean isEmpty(String content) {
        return content == null || "".equals(content);
    }
}
