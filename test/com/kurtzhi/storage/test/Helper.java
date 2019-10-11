package com.kurtzhi.storage.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.datatype.PdxGUID;
import com.kurtzhi.pedx.misc.ZoneDateTime;
import com.kurtzhi.pedx.sql.DatabaseHelper;

public class Helper {
    public static void main(String args[]) {
        ZoneDateTime zdt= new ZoneDateTime("-128-03", "+0000");
        Date dt = zdt.getDate();
        System.out.println(zdt.getISO8601DateWithTimezone("+0000"));
    }
}
        /*
    static DatabaseHelper _dbHelper = Pedx.getDatabaseHelper();
         * System.out.println(_dbHelper.create("test"));
         * System.out.println(_dbHelper.use("test"));
         * System.out.println(_TableHelper.generateCreateSQL(Device.class));
         * System.out.println(_TableHelper.generateCreateSQL(Person.class));
         * System
         * .out.println(_TableHelper.generateCreateSQL(HwProducter.class));
         * System.out.println(_TableHelper.generateCreateSQL(SwCompany.class));
         * System.out.println(_TableHelper.generateCreateSQL(Software.class));
         * System.out.println(_TableHelper.generateCreateSQL(WebSite.class));
         * System
         * .out.println(_TableHelper.generateCreateSQL(MarketRegion.class));
         * System.out.println(_TableHelper.generateCreateSQL(Seller.class));
         * System.out.println(_TableHelper.generateCreateSQL(Mobile.class));
         * System.out.println(_TableHelper.generateCreateSQL(PriceBoard.class));
         * System.out.println(_TableHelper.generateCreateSQL(TvChannel.class));
         * System.out.println(_TableHelper.generateCreateSQL(Stb.class));
         * System.
         * out.println(_TableHelper.generateCreateSQL(GameConsole.class));
         */

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
         */

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