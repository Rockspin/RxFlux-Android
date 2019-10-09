package com.rockspin.rxfluxcore

/**
 * Current state of an object. for example the state of a view or the state of a hardware device,
 * or the entire state of an application.
 */
interface State

/**
 * A result modifies state.
 */
interface Result

/**
 * An [Event] represents a UiEvent that a FluxView can generate.
 * For example:
 * - pressing a button create a button pressed event.
 * - the Button Pressed Event would create a Update Label Result.
 * - the Update Label Result would modify the LabelViewState.
 *
 * These generally are fed into a [ResultCreator] which creates one or more [Result]s when then are used to update [State].
 */
interface Event

/**
 * An [Effect] is an event that should be consumed only once, like a Snackbar message, a navigation event or a dialog trigger.
 * It is useful to avoid storing items in the view model that are not cached.
 */
interface Effect





