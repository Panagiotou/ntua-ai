% Global Imports
:- [nodes,
    lines,
    client,
    taxis,
    traffic].

% Node belongs to line L
belongsTo(X, Y, L) :-
  nodes(_, X, Y, L, _, _, _, _).

% Line is directed same as nodes
directed(L) :-
  lines(L, _, _, yes, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _).

% True when line is directed opposite
oppositeDirected(L) :-
  lines(L, _, _, -1, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _).

% True when line is undirected
undirected(L) :-
  lines(L, _, _, no, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _).

% Computes A = |Y - X|
abs(X, Y, A) :-
  X >= Y -> A is X - Y;
  A is Y - X.

% Get adjacent nodes based on graph
nextWithLine(X, Y, U, V, L) :-
  belongsTo(X, Y, L),
  belongsTo(U, V, L),
  nodes(I, X, Y, L, _, _, _, _),
  nodes(J, U, V, L, _, _, _, _),
  I =\= J,
  (
    directed(L) -> J is I + 1;
    oppositeDirected(L) -> J is I - 1;
    abs(J, I, 1)
  ).

next(X, Y, U, V) :- nextWithLine(X, Y, U, V, _).

nextAvoidingTraffic(X, Y, U, V, ClientId, TrafficToAvoid) :-
  nextWithLine(X, Y, U, V, L),
  client(ClientId, _, _, _, _, H:M, _, _, _),
  traffic(L,_,Traffics,_, _),
  avoidsTrafficTypes(Traffics, H:M, TrafficToAvoid).


% Get all goals
goal(ClientId, TaxiId) :-
  client(ClientId, _, _, _, _, _, Capacity, ClientLanguages, Luggage),
  taxis(X, Y, TaxiId, yes, Min-Max, TaxiLanguages, _, _, Type, _),
  between(Min, Max, Capacity),
  fitsLuggage(Type, Luggage),
  ssubset(ClientLanguages, TaxiLanguages).

% Get all goals for a client
goals(ClientId, Taxis) :-
  findall(T, goal(ClientId, T), Taxis).

% Check if sets are not disjoint
doesIntersect(X,Y) :-
  intersection(X,Y,Z),
  dif(Z,[]).

% Checks if Source is ssubset of Target
ssubset(Source, Target) :-
  (
  Source = [_ | _], Target = [_ | _] -> doesIntersect(Source, Target);
  Target = [_ | _] -> member(Source, Target);
  Source = Target
  ).

% Check is H3:M3 is in interval
betweenHours(H1:M1, H2:M2, H3:M3) :-
  X1 is 60 * H1 + M1,
  X2 is 60 * H2 + M2,
  X3 is 60 * H3 + M3,
  between(X1, X2, X3).

% Avoid Traffic
hasTraffic(Traffics, H:M, Type) :-
  member(H1:M1-H2:M2-Type, Traffics),
  betweenHours(H1:M1, H2:M2, H:M).

avoidsTrafficTypes(_, _, []).
avoidsTrafficTypes(_, _, nil).
avoidsTrafficTypes(Traffics, H:M, [Type | Rest]) :-
  \+hasTraffic(Traffics, H:M, Type),
  avoidsTrafficTypes(Traffics, H:M, Rest).

% Fits luggage if any
fitsLuggage(Type, Luggage) :-
  (
    Luggage = 1 -> member(Type, [large, subcompact]);
    true
  ).

% Closest Node
square(X, Y) :- Y is X * X.

% Eucledian Distance (squared) between (X, Y), (U, V)
eucledianDistance(X, Y, U, V, D) :-
  DX is X - U,
  DY is Y - V,
  square(DX, SDX),
  square(DY, SDY),
  D is SDX + SDY.


distanceFromNode(X, Y, D) :-
  nodes(_, U, V, _, _, _, _, _),
  eucledianDistance(X, Y, U, V, DD),
  D = (X, Y, DD).

minDistance(X, Y, X1, Y1, X2, Y2, Result) :-
  eucledianDistance(X, Y, X1, Y1, D1),
  eucledianDistance(X, Y, X2, Y2, D2),
  (
    D1 =< D2 -> Result = (X1, Y1);
    Result = (X2, Y2)
  ).


closestPointHelper(X, Y, I, (Xopt, Yopt), Result) :-
    (
      I < 0 -> Result = (Xopt, Yopt);
      nodes(I, U, V, _, _, _, _, _),
      minDistance(X, Y, U, V, Xopt, Yopt, (Xt, Yt)),
      J is I - 1,
      closestPointHelper(X, Y, J, (Xt, Yt), Result)
    ).

closestPoint(X, Y, U, V) :-
  numberOfPoints(N),
  N1 is N - 1,
  closestPointHelper(X, Y, N1, (10000, 10000), (U, V)).

isNode(I) :-  nodes(I, _, _, _, _, _, _, _).
numberOfPoints(N) :-
  findall(I, isNode(I), L),
  length(L, N).

getPoint(I, U, V) :- nodes(I, U, V, _, _, _, _, _).
