package dev.juliusabels.fish_fiesta.screens.overlay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class DialogButton implements Runnable {
    private String name;
}
