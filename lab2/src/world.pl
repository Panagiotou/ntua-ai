% Consults
:- [
    lines,
    client,
    taxis,
    next,
    traffic
    ].

% Node belongs to line L
belongsTo(X, Y, L) :- nextWithLine(X, Y, _, _, L).

% Line is directed same as nodes
directed(L) :-
  lineDirection(L, yes).

% True when line is directed opposite
oppositeDirected(L) :-
  lineDirection(L, -1).

% True when line is undirected
undirected(L) :-
  lineDirection(L, no).

% Next brings the adjacent nodes of a node
next(X, Y, U, V) :- nextWithLine(X, Y, U, V, _).

% Is true iff there is a path from X, Y to U, V
canMoveFromTo(X, Y, U, V) :-
  next(X, Y, P, Q),
  next(P, Q, U, V).

% Get adjacent nodes avoiding certain types of traffic
% Example nextAvoidingTraffic(23.7611292,37.9863578, U, V, 0, [high, medium])
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

% Avoid certain types of traffic
avoidsTrafficTypes(_, _, []).
avoidsTrafficTypes(_, _, mask).
avoidsTrafficTypes(Traffics, H:M, [Type | Rest]) :-
  \+hasTraffic(Traffics, H:M, Type),
  avoidsTrafficTypes(Traffics, H:M, Rest).

% Fits luggage if any
fitsLuggage(Type, Luggage) :-
  (
    Luggage = 1 -> member(Type, [large, subcompact]);
    true
  ).

% Default getters
getPoint(U, V) :- next(U, V, _, _).
getClient(I, X, Y, U, V) :- client(I, X, Y, U, V,_, _, _, _).
getTaxi(I, U, V) :-   taxis(U, V, I, _, _, _, _, _, _, _).
