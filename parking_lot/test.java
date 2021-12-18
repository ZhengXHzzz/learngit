package parking_lot;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class test {

    public static void entry() throws Exception {
        System.out.print("请输入进入停车场车辆的车牌号：");
        Scanner input=new Scanner(System.in);
        String Car_Number=input.next();
        car.Car_Entry(Car_Number);
    }
    public static void leave() throws Exception {
        System.out.print("请输入离开停车场车辆的车牌号：");
        Scanner input=new Scanner(System.in);
        String Car_Number=input.next();
        car.Car_Leave(Car_Number);
    }
    public static void find() throws Exception {
        System.out.print("请输入需要查询的车辆的车牌号：");
        Scanner input=new Scanner(System.in);
        String Car_Number=input.next();
        Staff_operation.whether_in_parking_lot(Car_Number);
    }
    public static void buy() throws Exception {
        System.out.print("请输入要购买车位的车辆的车牌号：");
        Scanner input=new Scanner(System.in);
        String Car_Number=input.next();
        Staff_operation.space_Recharge(Car_Number);
    }
    public static void main(String[] args) throws Exception {
        for(int i=0;i<10;i++)//连续10次购买车位
            buy();

        for(int i=0;i<5;i++)//连续5次续费车位
            buy();

        for(int i=0;i<10;i++)//连续10次固定车辆进场
            entry();

        for(int i=0;i<5;i++)//连续5次固定车辆离场
            leave();

        for(int i=0;i<10;i++)//连续10次临时车辆进场
            entry();

        for(int i=0;i<5;i++)//连续5次临时车辆离场
            leave();

        for(int i=0;i<10;i++)//查询车辆是否在停车场中
            find();
    }

}
