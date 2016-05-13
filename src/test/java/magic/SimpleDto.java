/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magic;

import basic.ListAdapter;
import org.bananarama.annotation.Banana;


/**
 *
 * @author Guglielmo De Concini
 */
@Banana(adapter = ListAdapter.class)
public class SimpleDto {
    private int id;
    private String date;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SimpleDto){
            SimpleDto dto = (SimpleDto) obj;
            return dto.id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    
}
