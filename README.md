# RxFlux
Flux framework for Kotlin/android

# What is RxFlux?

An opinionated, restrictive framework For Building iOS and Android Apps. 

* Implements [Flux](https://facebook.github.io/flux/) on Andorid and iOS with small modifications.

* Pure Kotlin/Swift framework.

* Uses RxJava/RXSwift to model asynchronous code.

* Use the Strictly types natures of Kotlin/Swift to save us from ourselves.


### Why RxFlux?

We created RxFlux when building our flagship product Curated, as a way to speed up development on two platform. 
 
 * Having a common framework and approach across both platforms made building new features simple.
 * Business logic in Kotlin could be quickly translated line by line to similar code in Swift.
 * Adding feature and API calls had a set pattern and type safety that made code reviews simple.
 * Because of the decoupled nature and unidirectional dataflow of flux tests we simple to write. 
 * When onboarding new staff we could just point them to the flux documentation to get started.

In short it made our lives easier and I was less likely to murder and/or lose a finger to my Co-founder.

### RxFlux: An Overview:

RxFlux consists of two main libraries RxFlux-Android and RxFlux-Swift. Both operate in the same way with the same class names and functionality. We will be covering the Kotlin/Android version below. Familiarity with flux will be useful in understanding this project its documentation can be accessed [here](https://facebook.github.io/flux/).

In RxFlux all data moves in one direction.

* Views generate events.

* Events are mapped to one or more results.

* Results are dispatched to stores via the dispatcher.

* The state is modifed by each emitted result

* The view is redrawn based on the current state.

![Dataflow](https://cdn-images-1.medium.com/max/3988/1*9Xl6yqjP2D4hacVDrUFYxQ.png)*Dataflow*

We modify this approach slightly adding Effects in to the mix. Effects are one off events that should be handled by views, we will demonstrate a worked example of effects later, but mostly that are used for navigation and error messages.

![](https://cdn-images-1.medium.com/max/4076/1*kwdjPMrnHk0KrzyAuOxX0Q.png)

RxFlux-Android is divided into two libraries, RxFlux-Android and RxFlux-Core. RxFlux-core is devoid of any android specific code and can be used in any Kotlin/Java project. RxFlux-Android extends classes in RxFlux-core to support the andorid platform and adds automatic subscription management for RxJava. The repo contains a brief example of how to implement this framework.

## Worked example

see wiki 

### TODO: Recipies

* How to serialise viewstate

* A ***MultiStore, ***that covers screens that are repeated in an app, i.e A profile screen for multiple users, Chat conversations. They all have the same business logic with different data.

* Making business logic simpler by spliting and combining reducer and Result creators

* How to combine Stores, Reducers and resultCreators.

* Creating a store that holds the state of a connected bluetooth device and conbining that store with others to update UI.
