package parking_lot;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Construct_where {
    ArrayList<String> conditions = new ArrayList<String>();
    // 加入单个查询条件
    public void add(String condition)
    {
        condition = condition.trim();
        if( !condition.startsWith("("))
        {
            condition= "(" + condition + ")" ;
        }

        conditions.add(condition);
    }

    // 加入有操作符的表达式
    public void add(String name, String op, Object value)
    {
        add( Construct_SQL.name(name) + op + Construct_SQL.value(value.toString()));
    }

    /////////////////////////////////
    // 字符串值 比较
    public void add2(String name, String value)
    {
        add (name, "=", value);
    }

    // 数值 比较
    public void add2(String name, Long value)
    {
        add (name, "=", value);
    }

    // 数值 比较
    public void add2(String name, Integer value)
    {
        add (name, "=", value);
    }

    // 数值 比较
    public void add2(String name, Short value)
    {
        add (name, "=", value);
    }

    public void add2(String name, Boolean value)
    {
        add (name, "=", value ? "1" :"0");
    }

    ////////////////////////////////

    // LIKE
    public void addLike(String name, String value)
    {
        //conditions.add( colName + " LIKE '%" + colValue + "%'");
        // 把中间的空格替换成%通配符
        String filter = value.replace('　', '%').replace(' ', '%').replace('，', '%'); // 中文空格
        add (name, " LIKE ", filter);
    }

    // IN
    //  WHERE id IN ('07922860270B47FDB02ADD231F885DAB', '08C74DB5EBC64F439BB747EECBEC3862')
    public void addIn(String name, List values)
    {
        if(values.size() == 0) return;

        String sql =  Construct_SQL.name(name) + " IN (";

        for(int i=0; i<values.size(); i++)
        {
            String s = Construct_SQL.value( values.get(i).toString());
            sql += s;
            if(i != values.size() - 1) sql += ",";
        }

        sql += ") ";
        add(sql);
    }

    public void addIn(String name, Object[] values)
    {
        if(values.length == 0) return;

        String sql = Construct_SQL.name(name) + "IN (";
        for(int i=0; i<values.length; i++)
        {
            String a = values[i].toString().trim();
            if(a.length() == 0) continue;

            String s = Construct_SQL.value(a);
            sql += s;
            if(i != values.length - 1) sql += ",";
        }

        sql += ")";
        add(sql);
    }

    @Override
    public String toString()
    {
        if(conditions.size() == 0) return " ";

        String where = " WHERE ";
        for(int i=0; i<conditions.size() ;i++)
        {
            if(i != 0) where += " AND ";
            where += conditions.get(i) ;
        }
        return where;
    }

}
