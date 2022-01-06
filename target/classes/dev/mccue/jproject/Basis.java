package dev.mccue.jproject;

import java.util.Objects;

public final class Basis {
    final Object rawBasisObject;

    Basis(Object rawBasisObject) {
        this.rawBasisObject = rawBasisObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basis basis)) return false;
        return Objects.equals(rawBasisObject, basis.rawBasisObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawBasisObject);
    }

    @Override
    public String toString() {
        return "Basis[" + rawBasisObject + "]";
    }
}
