package parking_lot;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class car {

    //计算费用
    public static int fees(int minutes){
        int fee=0;
        if(minutes<60) fee=0;
        else if(minutes>=60 && minutes<=1440)
            if(minutes%60==0){
                fee+=5*(minutes/60);
                if(fee>20)  fee=20;
            }
            else {
                fee += 5 * (minutes / 60 + 1);
                if(fee>20)  fee=20;
            }
        else if(minutes>1440)
            if(minutes%1440==0)
                fee+=20*(minutes/1440);
            else{
                fee+=20*(minutes/1440);
                int m=minutes%1440;
                int fee1=0;

                if(m%60==0){
                    fee1+=5*(m/60);
                    if(fee1>20)  fee1=20;
                }
                else {
                    fee1 += 5 * (m / 60 + 1);
                    if(fee1>20)  fee1=20;
                }
                fee+=fee1;
            }
        return fee;
    }

    //查询车辆是否为固定车辆
    public static ArrayList<String> Car_Number_Select(String Car_Number) throws  Exception{
        ArrayList<String> conditions = new ArrayList<String>(3);
        Connection connection=SQL_Connection.Connect();
        Statement stmt=connection.createStatement();

        Construct_where where =new Construct_where();
        where.add("Car_no="+'"'+Car_Number+'"');
        String sql="select * from Fixed_Car"+where;
        ResultSet resultSet=stmt.executeQuery(sql);

        String carno="";
        String name="";
        String date_remaining="";
        while(resultSet.next()) {
            carno = resultSet.getString(1);
            name = resultSet.getString(2);
            date_remaining = resultSet.getString(3);
        }

        conditions.add(carno);
        conditions.add(name);
        conditions.add(date_remaining);

        connection.close();
        return conditions;
    }

    //临时车辆进入
    public static int Temporary_car_Enter(String Car_Number) throws Exception {
        Connection conn=SQL_Connection.Connect();
        Statement stmt= (Statement) conn.createStatement();

        //查询临时车位数量
        String sql="select temporary from data_summary";
        ResultSet resultSet=stmt.executeQuery(sql);
        int temporary=0;
        while(resultSet.next())
            temporary=resultSet.getInt(1);

        //判断是否允许临时车辆进入
        if(temporary>0)//仍有车位
        {
            //获取当前时间
            SimpleDateFormat sdf=new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            String now_time =sdf.format(new Date());

            //记录停车场中的临时车辆
            Construct_Insert insert=new Construct_Insert("Temporary_car");
            insert.add2("Car_no",Car_Number);
            String sql1=insert.toString();
            stmt.execute(sql1);

            //临时车位数减少
            Construct_Update update=new Construct_Update("data_summary");
            update.add2("temporary",temporary-1);
            String sql2=update.toString();
            stmt.execute(sql2);

            //添加车辆进入记录
            Construct_Insert insert1=new Construct_Insert("record");
            insert1.add2("carno",Car_Number);
            insert1.add2("sign","F");
            insert1.add2("entry_time",now_time);
            insert1.add2("departure_time", "0000-00-00 00:00:00");
            insert1.add2("sign_in","Y");
            insert1.add2("fees",0);
            String sql3=insert1.toString();
            stmt.execute(sql3);

            conn.close();
            return temporary;
        }
        else//车位已满
            return -1;

    }

    //固定车辆进入
    public static void Fixed_car_Enter(String Car_Number) throws Exception{
        Connection connection=SQL_Connection.Connect();
        Statement stmt=connection.createStatement();

        //获取当前时间
        SimpleDateFormat sdf=new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String now_time=sdf.format(new Date());

        //添加车辆进入记录
        Construct_Insert insert=new Construct_Insert("record");
        insert.add2("carno",Car_Number);
        insert.add2("sign","T");
        insert.add2("entry_time",now_time);
        insert.add2("departure_time", "0000-00-00 00:00:00");
        insert.add2("sign_in","Y");
        insert.add2("fees",0);
        String sql2=insert.toString();
        stmt.execute(sql2);

        connection.close();
    }

    //临时车辆离场
    public static int Temporary_Car_Leave(String Car_Number) throws Exception{
        Connection connection=SQL_Connection.Connect();
        Statement stmt=connection.createStatement();

        //获取离开时间
        SimpleDateFormat sdf=new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String now_time =sdf.format(new Date());

        //删除临时车辆中的数据
        String c='"'+Car_Number+'"';
        String sql="delete from temporary_car where Car_no="+c;
        stmt.execute(sql);

        //计算停车时间
        Construct_where where=new Construct_where();
        where.add2("carno",Car_Number);
        where.add2("sign_in","Y");
        sql="select entry_time from record"+where;
        ResultSet resultSet=stmt.executeQuery(sql);
        String entry_time="";
        while(resultSet.next()){
        entry_time= resultSet.getString(1);
        }
        Date now_time1=sdf.parse(now_time);
        Date entry_time1=sdf.parse(entry_time);
        long now_time2=now_time1.getTime();
        long entry_time2=entry_time1.getTime();
        int minutes=(int)((now_time2-entry_time2)/(1000*60));

        //计算费用
        int fees=fees(minutes);


        //更新停车记录
        Construct_Update update=new Construct_Update("record");
        update.add2("departure_time",now_time);
        update.add2("sign_in","N");
        update.add2("fees",fees);
        sql=update.toString()+where;
        stmt.execute(sql);

        //临时车位数量增加
        String sql1="select temporary from data_summary";
        ResultSet resultSet1=stmt.executeQuery(sql1);
        int temporary=0;
        while(resultSet1.next())
            temporary=resultSet1.getInt(1);
        Construct_Update update1=new Construct_Update("data_summary");
        update1.add2("temporary",temporary+1);
        String sql2=update1.toString();
        stmt.execute(sql2);

        connection.close();
        return fees;
    }

    //固定车辆离场
    public static void Fixed_car_Leave(String Car_Number) throws Exception{
        Connection connection=SQL_Connection.Connect();
        Statement stmt=connection.createStatement();

        //获取离开时间
        SimpleDateFormat sdf=new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String now_time =sdf.format(new Date());

        Construct_where where=new Construct_where();
        where.add2("carno",Car_Number);
        where.add2("sign_in","Y");
        Construct_Update update=new Construct_Update("record");
        update.add2("departure_time",now_time);
        update.add2("sign_in","N");
        String sql=update.toString()+where;
        stmt.execute(sql);
        connection.close();
    }

    //车辆进入停车场
    public static void Car_Entry(String Car_Number ) throws Exception {
        ArrayList<String> conditions = Car_Number_Select(Car_Number);
        if(Car_Number.equals(conditions.get(0))){
            Fixed_car_Enter(Car_Number);
            System.out.println("欢迎回家！剩余天数为："+conditions.get(2));
        }
        else{
            int temporary=Temporary_car_Enter(Car_Number);
            if(temporary!=-1)
                System.out.println("临时车辆进入，剩余车位："+temporary);
            else
                System.out.println("车位已满");
        }
    }

    //车辆离开停车场
    public static void Car_Leave(String Car_Number) throws  Exception{
        ArrayList<String> conditions = Car_Number_Select(Car_Number);
        if(Car_Number.equals(conditions.get(0))){
            Fixed_car_Leave(Car_Number);
            System.out.println("一路顺风！剩余天数为："+conditions.get(2));
        }
        else{
            int fees=Temporary_Car_Leave(Car_Number);
            System.out.println("一路顺风！预缴纳费用为："+fees);
        }
    }
}
