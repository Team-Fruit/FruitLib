package net.teamfruit.lib;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    public final static
    @Nonnull
    Logger log = LogManager.getLogger(Reference.NAME);
}