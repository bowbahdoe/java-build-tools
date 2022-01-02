package dev.mccue.jproject;

public sealed interface ConflictHandler<State> {
    record Ignore<State>() implements ConflictHandler<State> {}
    record Overwrite<State>() implements ConflictHandler<State> {}
    record Append<State>() implements ConflictHandler<State> {}
    record AppendDedupe<State>() implements ConflictHandler<State> {}
    record DataReaders<State>() implements ConflictHandler<State> {}
    record Warn<State>() implements ConflictHandler<State> {}
    record Error<State>() implements ConflictHandler<State> {}

    non-sealed interface UserDefined<State> extends ConflictHandler<State> {
        ConflictResolution<State> resolve(
                ConflictInformation conflictInformation
        );
    }
}
