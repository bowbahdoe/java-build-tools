package dev.mccue.jproject;

import java.io.File;
import java.io.InputStream;

public record ConflictInformation(
        String path,
        InputStream in,
        File existing,
        String lib
        // Map<Object, Object> state
) {

}
