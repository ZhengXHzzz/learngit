package parking_lot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Staff_operation {
    //固定车位购买及续费
    public static void space_Recharge(String Car_Number) throws Exception {

        Connection conn=SQL_Connection.Connect();
        Statement stmt=conn.createStatement();
        System.out.print("请输入要续费的月数");
        Scanner input = new Scanner(System.in);
        int month = input.nextInt();

        int fees=month*50;

        ArrayList<String> conditions = car.Car_Number_Select(Car_Number);
        if(Car_Number.equals(conditions.get(0))){
            //车位续费
            System.out.println("车位续费，需缴纳费用为："+fees+"元");
            Construct_where where =new Construct_where();
            where.add2("Car_no",Car_Number);
            String sql="select Date_remaining from Fixed_Car"+where;
            ResultSet rs=stmt.executeQuery(sql);

            int day_remaining=0;
            rs.next();
            day_remaining=rs.getInt(1)+month*30;

            Construct_Update update=new Construct_Update("fixed_car");
            update.add2("date_remaining",day_remaining);
            sql=update.toString()+where;
            stmt.execute(sql);
            System.out.println("续费操作已完成");
        }
        else{
            //车位购买
            System.out.print("请输入车主姓名");
            String name=input.next();
            System.out.println("车位购买,需缴纳费用为："+fees+"元");
            Construct_Insert insert=new Construct_Insert("Fixed_car");
            insert.add2("Car_no",Car_Number);
            insert.add2("Name",name);
            insert.add2("date_remaining",month*30);

            String sql=insert.toString();
            stmt.execute(sql);

            //更新各种车位数量
            String sql1="select fixed,temporary from data_summary";
            ResultSet resultSet1=stmt.executeQuery(sql1);
            int fixed=0;
            int temporary=0;
            while(resultSet1.next()) {
                fixed = resultSet1.getInt(1);
                temporary = resultSet1.getInt(2);
            }
            Construct_Update update1=new Construct_Update("data_summary");
            update1.add2("fixed",fixed+1);
            update1.add2("temporary",temporary-1);
            String sql2=update1.toString();
            stmt.execute(sql2);
            System.out.println("购买操作已完成");
        }

        conn.close();
    }

    //查询某车辆是否在停车场中
    public static void whether_in_parking_lot(String Car_Number) throws Exception{
        Connection conn=SQL_Connection.Connect();
        Statement stmt=conn.createStatement();

        Construct_where where=new Construct_where();
        where.add2("carno",Car_Number);
        String sql="select carno,entry_time,departure_time,sign_in from record"+where;
        ResultSet rs=stmt.executeQuery(sql);
        if(!rs.next())
            System.out.println("该车辆未进过停车场");
        String carno="";
        String entry_time="";
        String departure_time="";
        String sign_in="";
        rs.beforeFirst();
        while(rs.next())
        {
            carno=rs.getString(1);
            entry_time=rs.getString(2);
            departure_time=rs.getString(3);
            sign_in=rs.getString(4);
            if(sign_in.equals("Y")) {
                System.out.println("车辆：" + carno + "在停车场中");
                System.out.println("进入时间为"+entry_time);
                break;
            }

        }

        if(sign_in.equals("N")) {
            System.out.println("车辆：" + carno + "已离开停车场");
            System.out.println("进入时间为"+entry_time);
            System.out.println("离开时间为"+departure_time);
        }
        conn.close();
    }

}
