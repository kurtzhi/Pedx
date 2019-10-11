package com.kurtzhi.storage.test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.kurtzhi.pedx.Condition;
import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.Query;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxBlob;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.expression.Eq;
import com.kurtzhi.pedx.expression.EqsAnd;
import com.kurtzhi.pedx.misc.W3cDate;
import com.kurtzhi.pedx.sql.DatabaseHelper;
import com.kurtzhi.pedx.sql.TableHelper;
import com.kurtzhi.storage.test.jdo.Device;
import com.kurtzhi.storage.test.jdo.GameConsole;
import com.kurtzhi.storage.test.jdo.HardwareProducer;
import com.kurtzhi.storage.test.jdo.MarketRegion;
import com.kurtzhi.storage.test.jdo.MobilePhone;
import com.kurtzhi.storage.test.jdo.Operator;
import com.kurtzhi.storage.test.jdo.PriceBoard;
import com.kurtzhi.storage.test.jdo.Seller;
import com.kurtzhi.storage.test.jdo.Software;
import com.kurtzhi.storage.test.jdo.SoftwareCompany;
import com.kurtzhi.storage.test.jdo.Stb;
import com.kurtzhi.storage.test.jdo.TvChannel;
import com.kurtzhi.storage.test.jdo.WebSite;

public class Helper {
    static DatabaseHelper _dbHelper = Pedx.getDatabaseHelper();
    static TableHelper _TableHelper = Pedx.getTableHelper();
    public static void main(String args[]) {
        /*
        System.out.println(_dbHelper.create("test"));
        System.out.println(_dbHelper.use("test"));
        System.out.println("drop table " + _TableHelper.translateTableName(Operator.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(Device.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(HardwareProducer.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(SoftwareCompany.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(Software.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(WebSite.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(MarketRegion.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(Seller.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(MobilePhone.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(PriceBoard.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(TvChannel.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(Stb.class) + ";");
        System.out.println("drop table " + _TableHelper.translateTableName(GameConsole.class) + ";");
        System.out.println(_TableHelper.generateCreateSQL(Device.class));
        System.out.println(_TableHelper.generateCreateSQL(HardwareProducer.class));
        System.out.println(_TableHelper.generateCreateSQL(SoftwareCompany.class));
        System.out.println(_TableHelper.generateCreateSQL(Software.class));
        System.out.println(_TableHelper.generateCreateSQL(WebSite.class));
        System.out.println(_TableHelper.generateCreateSQL(MarketRegion.class));
        System.out.println(_TableHelper.generateCreateSQL(Seller.class));
        System.out.println(_TableHelper.generateCreateSQL(MobilePhone.class));
        System.out.println(_TableHelper.generateCreateSQL(PriceBoard.class));
        System.out.println(_TableHelper.generateCreateSQL(TvChannel.class));
        System.out.println(_TableHelper.generateCreateSQL(Stb.class));
        System.out.println(_TableHelper.generateCreateSQL(GameConsole.class));
        */

        //System.out.println(Pedx.getTableHelper().generateCreateSQL(Operator.class));
        /*
         * Device d1 = new Device(); d1.deviceName.set("Smart Phone");
         * d1.save();
         * 
         * Device d2 = new Device(); d2.deviceName.set("Video Game Console");
         * d2.save();
         * 
         * Person p1 = new Person(); p1.personName.set("Tim Cook"); p1.save();
         * 
         * Person p2 = new Person(); p2.personName.set("Steve Ballmer");
         * p2.save();
         * 
         * Person p3 = new Person(); p3.personName.set("Lei Jun"); p3.save();
         * 
         * Person p4 = new Person(); p4.personName.set("Zhou Hongyi");
         * p4.save();
         * 
         * Person p5 = new Person(); p5.personName.set("Larry Page"); p5.save();
         * 
         * Person p6 = new Person(); p6.personName.set("Kun Hee Lee");
         * p6.save();
         * 
         * HwProducter h1 = new HwProducter(); h1.hwProducterName.set("360");
         * h1.hwProducterCeoId.set(p4.personId.get()); h1.save();
         * 
         * HwProducter h2 = new HwProducter(); h2.hwProducterName.set("XiaoMi");
         * h2.hwProducterCeoId.set(p3.personId.get()); h2.save();
         * 
         * HwProducter h3 = new HwProducter(); h3.hwProducterName.set("Apple");
         * h3.hwProducterCeoId.set(p1.personId.get()); h3.save();
         * 
         * HwProducter h4 = new HwProducter();
         * h4.hwProducterName.set("Microsoft");
         * h4.hwProducterCeoId.set(p2.personId.get()); h4.save();
         * 
         * HwProducter h5 = new HwProducter();
         * h5.hwProducterName.set("Samsung");
         * h5.hwProducterCeoId.set(p6.personId.get()); h5.save();
         * 
         * SwCompany s1 = new SwCompany(); s1.swCompanyName.set("Microsoft");
         * s1.swCompanyCeoId.set(p2.personId.get()); s1.save();
         * 
         * SwCompany s2 = new SwCompany(); s2.swCompanyName.set("Apple");
         * s2.swCompanyCeoId.set(p1.personId.get()); s2.save();
         * 
         * SwCompany s3 = new SwCompany(); s3.swCompanyName.set("Google");
         * s3.swCompanyCeoId.set(p5.personId.get()); s3.save();
         * 
         * Software ss1 = new Software();
         * ss1.softwareCompany.set(s1.swCompanyId.get());
         * ss1.softwareName.set("Windows Phone 8"); ss1.save();
         * 
         * Software ss2 = new Software();
         * ss2.softwareCompany.set(s2.swCompanyId.get());
         * ss2.softwareName.set("iOS 5"); ss2.save();
         * 
         * Software ss3 = new Software();
         * ss3.softwareCompany.set(s3.swCompanyId.get());
         * ss3.softwareName.set("Andorid 4.0"); ss3.save();
         * 
         * Software ss4 = new Software();
         * ss4.softwareCompany.set(s1.swCompanyId.get());
         * ss4.softwareName.set("Windows 8"); ss4.save();
         * 
         * MarketRegion m1 = new MarketRegion(); m1.regionName.set("China");
         * m1.save();
         * 
         * MarketRegion m2 = new MarketRegion(); m2.regionName.set("Japan");
         * m2.save();
         * 
         * MarketRegion m3 = new MarketRegion(); m3.regionName.set("India");
         * m3.save();
         * 
         * MarketRegion m4 = new MarketRegion();
         * m4.regionName.set("United State"); m4.save();
         * 
         * Seller sl1 = new Seller(); sl1.regionId.set(m1.regionId.get());
         * sl1.sellerName.set("365buy"); sl1.save();
         * 
         * Seller sl2 = new Seller(); sl2.regionId.set(m1.regionId.get());
         * sl2.sellerName.set("Taobao"); sl2.save();
         * 
         * Seller sl3 = new Seller(); sl3.regionId.set(m1.regionId.get());
         * sl3.sellerName.set("Vancl"); sl3.save();
         * 
         * Mobile mb1 = new Mobile(); mb1.deviceId.set(d1.deviceId.get());
         * mb1.mobileProducer.set(h3.hwProducterId.get());
         * mb1.mobileSeller.set(sl1.sellerId.get());
         * mb1.OS.set(ss2.softwareId.get()); mb1.mobileName.set("iPhone 4S");
         * mb1.save();
         * 
         * Mobile mb2 = new Mobile(); mb2.deviceId.set(d1.deviceId.get());
         * mb2.mobileProducer.set(h4.hwProducterId.get());
         * mb2.mobileSeller.set(sl2.sellerId.get());
         * mb2.OS.set(ss4.softwareId.get()); mb2.mobileName.set("Surface");
         * mb2.save();
         * 
         * Mobile mb3 = new Mobile(); mb3.deviceId.set(d1.deviceId.get());
         * mb3.mobileProducer.set(h5.hwProducterId.get());
         * mb3.mobileSeller.set(sl3.sellerId.get());
         * mb3.OS.set(ss3.softwareId.get());
         * mb3.mobileName.set("Galaxy S Tab 2"); mb3.save();
         * 
         * PriceBoard pb1 = new PriceBoard();
         * pb1.sellerId.set(sl1.sellerId.get());
         * pb1.mobileId.set(mb1.mobileId.get()); pb1.price.set(4700.00);
         * pb1.save();
         * 
         * PriceBoard pb2 = new PriceBoard();
         * pb2.sellerId.set(sl2.sellerId.get());
         * pb2.mobileId.set(mb2.mobileId.get()); pb2.price.set(3600.00);
         * pb2.save();
         * 
         * PriceBoard pb3 = new PriceBoard();
         * pb3.sellerId.set(sl3.sellerId.get());
         * pb3.mobileId.set(mb3.mobileId.get()); pb3.price.set(2799.00);
         * pb3.save();
         * 
         * WebSite ws1 = new WebSite();
         * ws1.softwareCompany.set(s3.swCompanyId.get());
         * ws1.websiteAddr.set("google.com"); ws1.save();
         * 
         * WebSite ws2 = new WebSite();
         * ws2.softwareCompany.set(s2.swCompanyId.get());
         * ws2.websiteAddr.set("apple.com"); ws2.save();
         * 
         * WebSite ws3 = new WebSite();
         * ws3.softwareCompany.set(s1.swCompanyId.get());
         * ws3.websiteAddr.set("microsoft.com"); ws3.save();
         * 
         * WebSite ws4 = new WebSite();
         * ws4.softwareCompany.set(s1.swCompanyId.get());
         * ws4.websiteAddr.set("live.com"); ws4.save();
         * 
         * WebSite ws5 = new WebSite();
         * ws5.softwareCompany.set(s1.swCompanyId.get());
         * ws5.websiteAddr.set("outlook.com"); ws5.save();
         * 
         * WebSite ws6 = new WebSite();
         * ws6.softwareCompany.set(s3.swCompanyId.get());
         * ws6.websiteAddr.set("gmail.com"); ws6.save(); TvChannel tc1 = new
         * TvChannel(); tc1.channelName.set("Fox"); tc1.save();
         * 
         * TvChannel tc2 = new TvChannel(); tc2.channelName.set("CBS");
         * tc2.save();
         * 
         * GameConsole gc1 = new GameConsole();
         * gc1.gcProducer.set(h4.hwProducterId.get());
         * gc1.tvChannel.set(tc2.channelId.get()); gc1.gcName.set("XBox");
         * gc1.save();
         * 
         * Stb st1 = new Stb(); st1.channel.set(tc2.channelId.get());
         * st1.stbName.set("AppleTV"); st1.save();
         * 
         * Stb st2 = new Stb(); st2.channel.set(tc1.channelId.get());
         * st2.stbName.set("Google TV"); st2.save();
         */
        /*
        Class<?>[] dbos = new Class<?>[] { WebSite.class, Software.class,
                MobilePhone.class, TvChannel.class, Seller.class, PriceBoard.class,
                HarewareProducer.class, SwCompany.class, Device.class, Person.class,
                Stb.class, GameConsole.class };
         * HwProducter.class, SwCompany.class, WebSite.class, Device.class,
         * Software.class, Person.class, Stb.class, MarketRegion.class
         */
        //Condition cond = null;
        // Condition cond = new Condition(new Eq(HwProducter.class,
        // "hwProducterId", "36b9b98b-bd99-44a3-a065-e69b32a27c81"));
        // Condition cond = new Condition(new Eq(HwProducter.class,
        // "hwProducterName", "Samsung"));
        // Condition cond = new Condition(new Eq(Software.class, "softwareName",
        // "iOS 5"));
        // Condition cond = new Condition(new Eq(MarketRegion.class,
        // "regionName", "China"));

        /*
         * System.out.println(selector.fetchField("deviceName", 0));
         * System.out.println(selector.fetchField("personName", 0));
         * System.out.println(selector.fetchField("swCompanyName", 0));
         * System.out.println(selector.fetchField("softwareName", 0));
         * System.out.println(selector.fetchField("personName", 0));
         * 
         */

        System.out.println(Pedx.getTableHelper().generateCreateSQL(Operator.class));

        /*
         * SQL Server 2012: set[N]?String, get[N]?String
         * Oracle 11g: get[N]?String
         * DB2 V10: [{set}|{get}]{1}String
         * MySQL: [{set}|{get}]{1}[N]?String
         */
        
        String on = "ΔЙק‎م๗あ叶葉and말";
        String os = "الأم الأب القمر أحد بلدان العالم حب الناس";
        os += "אנשי עולם אהבת ארץ ירח אמא אבא יום ראשון";
        os += "ماں، باپ سورج چاند لوگ، عوام ملک دنیا کی محبت";
        os += "مادر پدر یکشنبه مردم کشور ماه عشق جهان";
        os += "Мати батька сонце луна люди країни світу любові";
        os += "Мать отца солнце луна люди страны мира любви";
        os += "майка баща Слънце Луна страната свят любов";
        os += "Маці бацькі сонца месяц людзі краіны свету любові";
        os += "พ่อแม่ดวงอาทิตย์ดวงจันทร์คนรักประเทศโลก";
        os += "తల్లి తండ్రి సూర్యుడు చంద్రుడు ప్రజలు దేశం ప్రపంచంలో ప్రేమ";
        os += "ತಾಯಿ ತಂದೆ ಸೂರ್ಯ ಚಂದ್ರ ಜನರು ದೇಶದ ವಿಶ್ವದ ಪ್ರೀತಿ";
        os += "தாய் தந்தை சூரிய சந்திர மக்கள் நாட்டின் உலக காதல்";
        os += "la madre del padre de Sun Moon país la gente le encanta el mundo";
        os += "missier omm Ħad nies qamar pajjiż fid-dinja l-imħabba";
        os += "мајка татко сонцето месечината луѓето земјата свет љубов";
        os += "어머니가 아버지 일 달 사람들이 국가 세계 사랑";
        os += "母父日満月の人の国、世界の愛";
        os += "माता पिता चाँद लोगों को देश दुनिया प्यार रवि";
        os += "માતા પિતા ચંદ્ર લોકો દેશ વિશ્વમાં પ્રેમ સૂર્ય";
        os += "Η μητέρα του πατέρα Sun Moon άνθρωποι αγάπη χώρα του κόσμου";
        os += "დედა მამა მზე მთვარე ადამიანების ქვეყანა მსოფლიოში გაცნობების";
        os += "母親父親太陽月球人的國家世界上的愛";
        os += "母亲父亲太阳月球人的国家世界上的爱";
        os += "মা বাবা সূর্য চাঁদ লেগেছে বিশ্বের দেশ প্রেম";
        os += "մայրը, հայրը կիր moon մարդիկ երկիրը աշխարհում հարցաթերթիկը ծանոթյություններ";
        byte[] permissions = new byte[]{1,0,1,0,0,1};
        System.out.println(os.length());
        byte[] bs;
        try {
            bs = os.getBytes("UTF-8");
            System.out.println(bs.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*
        Operator oper1 = new Operator();
        oper1.operatorBirth.set(new W3cDate("1982-03-31", "+8"));
        oper1.operatorName.set(on);
        oper1.operatorFile.set(os);
        oper1.operatorPermissions.set(permissions);;
        oper1.save();
        
        Operator oper2 = new Operator();
        oper2.operatorName.set(on);
        oper2.load();
        System.out.println(oper2.operatorName.get());
        System.out.println(oper2.operatorFile.get());
        System.out.println(oper2.operatorBirth.get().getUTCDateWithEra());
        System.out.println(oper2.operatorCreationtime.get().getDateTimeWithEraByTimezone(" ", "+0800"));
        oper2.operatorName.set("支国栋");
        oper2.operatorFile.set("百度百科中的词条正文与判断内容均由用户提供，不代表百度百科立场。如果您需要解决具体问题（如法律、医学等领域），建议您咨询相关领域专业人士。");
        oper2.save();
        Operator oper3 = new Operator();
        oper3.operatorName.set("支国栋");
        if (oper3.load()) {
            oper3.operatorName.set(on);
            oper3.operatorFile.set(os);
            oper3.save();
        }
        Operator oper4 = new Operator();
        oper4.operatorId.set(oper3.operatorId.get());
        if (oper4.load()) {
            System.out.println(oper4.operatorName);
            System.out.println(oper4.operatorFile);
            System.out.println(oper4.operatorAge);
        }
        */
        W3cDate birth = new W3cDate("1980-07-13", "+8");
        W3cDate createTime = new W3cDate("2012-09-10 07:58:09", "+8");
        W3cDate wd = new W3cDate("1980-07-13", "0");
        PdxDate pd = new PdxDate();
        pd.set(wd);
        Query selector = new Query(Operator.class, new Condition(new Eq(Operator.class, "operatorBirth", pd)), null);
        //Selector selector = new Selector(Operator.class, new Condition(new Eq(Operator.class, "operatorName", on)), null);
        selector.select();
        if (selector.count() > 0) {
            System.out.println(selector.count());
            Operator oper5 = (Operator)selector.getDbo(Operator.class, 0);
            System.out.println(oper5.operatorName.get());
            System.out.println(selector.getField("operatorBirth", 0));
            System.out.println(oper5.operatorCreationtime.get().getUTCDateTime(" "));
        }
        
        /*
Eq eq1 = new Eq(MobilePhone.class, "mobilePhoneName", "iPhone 4S");
Eq eq2 = new Eq(MarketRegion.class, "regionName", "China");

EqsAnd ea = new EqsAnd(Operator.class, 
        new String[]{"operatorName", "operatorId"}, 
        new Object[]{"scott", "40f7e2d2-d7bc-4e19-be75-34ab3dbc2e83"});
Class<?>[] dboClasses = new Class<?>[]{ MobilePhone.class,
                                        Seller.class,
                                        HarewareProducer.class,
                                        MarketRegion.class };
Selector selector = new Selector(dboClasses, new Condition(ea), null);
selector.select();

if (selector.count() > 0) {
    Map<String, Object> row = selector.fetchRow(0);
    System.out.println(row.get("sellerName"));
} */
        
        /*
        Selector selector = new Selector(dbos, cond, null, 1);
        selector.batchSelect(2);
        System.out.println(selector.count());
        System.out.println(selector.countBatch());
        System.out.println(selector.fetchField("mobileName", 0));
        System.out.println(selector.fetchField("gcName", 0));
        System.out.println(selector.fetchField("stbName", 0));
        System.out.println(selector.fetchField("regionName", 0));
        System.out.println(selector.fetchField("softwareName", 0));
        System.out.println(selector.fetchField("websiteAddr", 0));
         * 
         * 
         * Provider provider = (Provider) selector.getDbo(Provider.class, 0);
         * System.out.println(provider.providerId);
         * System.out.println(provider.providerAlias);
         * System.out.println(provider.providerName);
         * System.out.println(provider.providerCreateTime);
         * 
         * User user = (User) selector.getDbo(User.class, 0);
         * System.out.println(user.userId); System.out.println(user.userName);
         */
    }
}