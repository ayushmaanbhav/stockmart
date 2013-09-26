package client;

import java.util.*;
class Companies
{
    static List<Company> comp;
    static Double sensex;
    static Company getCompanyWithName(String name)
    {
        for(int i=0;i<comp.size();i++)
        {
            if(comp.get(i).name.equalsIgnoreCase(name))
                return comp.get(i);
        }
        return null;
    }
    static Company getCompanyWithId(int id)
    {
        for(int i=0;i<comp.size();i++)
        {
            if(comp.get(i).id==id)
                return comp.get(i);
        }
        return null;
    }
    static void updateValues(int id, String name , double m, double i, double h, double l)
    {
        try{
            getCompanyWithId(id).updateData(m,h,l);
        }catch(Exception e)
        {
            comp.add(new Company(name,m,i,h,l,id));
        }
    }
    static void removeDuplicates()
    {
        for(int i=0;i<comp.size();i++)
        {
            for(int j=0;j<comp.size();j++)
            {
                if(comp.get(i).id==comp.get(j).id)
                {
                    comp.remove(j);
                }
            }
        }
    }
}