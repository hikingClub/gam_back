package com.gam.hikingclub.event;

import com.gam.hikingclub.entity.Modified;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;



@Getter
public class ModifiedEvent extends ApplicationEvent {
    private final Modified modified;

    public ModifiedEvent(Object source, Modified modified) {
        super(source);
        this.modified = modified;
    }
}
