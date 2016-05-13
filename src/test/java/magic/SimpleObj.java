/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package magic;

import java.time.LocalDate;
import org.bananarama.annotation.MapWith;

/**
 *
 * @author Guglielmo De Concini
 */
@MapWith(SimpleMapper.class)
public class SimpleObj {
    private int id;
    private LocalDate date;
    
    public SimpleObj(){
        
    }
    
    public SimpleObj(int id){
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SimpleObj){
            SimpleObj so = (SimpleObj)obj;
            return (so.id == id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    
}
