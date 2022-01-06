package dev.mccue.jproject;

import java.util.function.Function;

/**
 * A conflict handler defines how to resolve a conflict between
 * files when building an uberjar.
 *
 * @param <State> State to propagate between calls.
 */
public sealed interface ConflictHandler<State> {
    record Ignore<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Ignore<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Ignore<>();
        }
    }
    record Overwrite<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Overwrite<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Overwrite<>();
        }
    }
    record Append<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Append<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Append<>();
        }
    }
    record AppendDedupe<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> AppendDedupe<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new AppendDedupe<>();
        }
    }
    /* record DataReaders<State>() implements ConflictHandler<State> {} */
    record Warn<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Warn<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Warn<>();
        }
    }

    record Error<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Error<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Error<>();
        }
    }

    record Default<State>() implements ConflictHandler<State> {
        @Override
        public <NewState> Default<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return new Default<>();
        }
    }

    non-sealed interface UserDefined<State> extends ConflictHandler<State> {
        ConflictResolution<State> resolve(
                ConflictInformation<State> conflictInformation
        );

        @Override
        default <NewState> UserDefined<NewState> mapState(
                Function<? super NewState, ? extends State> from,
                Function<? super State, ? extends NewState> to
        ) {
            return conflictInformation -> this.resolve(conflictInformation.mapState(from))
                    .mapState(to);
        }
    }

    <NewState> ConflictHandler<NewState> mapState(
            Function<? super NewState, ? extends State> from,
            Function<? super State, ? extends NewState> to
    );
}
