package ru.frostman.mvc;

/**
 * @author slukjanov aka Frostman
 */
public interface Expression<R, C> {

    R getResult(C context);

}
