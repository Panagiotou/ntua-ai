import sys

px, py, dx, dy = map(float, sys.argv[1:5])
n = int(sys.argv[5])
try:
    name = sys.argv[6]
except:
    name = ""

squares = []
pts = [(px, py), (px + dx, py), (px + dx, py + dy), (px, py + dy), (px, py)]
squares.append(pts)

px += dx

for j in range(n - 1):
    pts = [(px, py), (px + dx, py), (px + dx, py + dy), (px, py + dy)]
    squares.append(pts)
    px += dx

with open(name + 'nodes.csv', 'w+') as f:
    f.write('X,Y,id,name\n')
    i = 1
    for pts in squares:
        for p, q in zip(pts, pts[1:]):
            f.write('{},{},{}\n'.format(p[0], p[1], i))
            f.write('{},{},{}\n'.format(q[0], q[1], i))
            i += 1

with open(name + 'taxis.csv', 'w+') as f:
    f.write('X,Y,id\n')
    f.write('{},{},1\n'.format(pts[0][0], pts[0][1]))

with open(name + 'client.csv', 'w+') as f:
    f.write('X,Y\n')
    f.write('{},{}\n'.format(squares[-1][-2][0], squares[-1][-2][1]))

