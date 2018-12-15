import sys

px, py, dx, dy = map(float, sys.argv[1:5])
name = sys.argv[5]

pts = [(px, py), (px + dx, py), (px + dx, py + dy), (px, py + dy)]

with open(name + 'nodes.csv', 'w+') as f:
    f.write('X,Y,id,name\n')
    i = 1
    for p, q in zip(pts, pts[1:]):
        f.write('{},{},{}\n'.format(p[0], p[1], i))
        f.write('{},{},{}\n'.format(q[0], q[1], i))
        i += 1

with open(name + 'taxis.csv', 'w+') as f:
    f.write('X,Y,id\n')
    f.write('{},{},1\n'.format(pts[0][0], pts[0][1]))

with open(name + 'clients.csv', 'w+') as f:
    f.write('X,Y\n')
    f.write('{},{}\n'.format(pts[-2][0], pts[-2][1]))

