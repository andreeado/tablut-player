def free_path_to_escape(king_pos, board):
    """Checks for unobstructed paths from King to escape positions."""
    paths = []
    x, y = king_pos

    for ex, ey in escapes:
        if x == ex:  # Same row
            if all(board[x,col] == '-' and features[x,col] not in ('C', 'T')
                for col in range(min(y, ey) + 1, max(y, ey))):
                paths.append((ex, ey))
        elif y == ey:  # Same column
            if all(board[row,y] == '-' and features[row,y] not in ('C', 'T')
                for row in range(min(x, ex) + 1, max(x, ex))):
                paths.append((ex, ey))
    return (paths)

def board_metrics(board): 
    # Find pieces and compute metrics
    king_pos = None
    white_pieces = 0
    black_pieces = 0

    for x in range(size):
        for y in range(size):
            piece = board[x,y]
            if piece == 'W':
                white_pieces += 1
            elif piece == 'B':
                black_pieces += 1
            elif piece == 'K':
                king_pos = (x, y)

    if king_pos is None:
        raise ValueError("King not found on the board!")

    # 1. 2*White pieces - Black pieces
    piece_difference = 2 * white_pieces - black_pieces

    # 2. King's Manhattan distance to nearest escape
    king_escape_distance = lookup_table[king_pos]

    # 3. King's danger metric: number of sides flanked by enemies or buildings
    danger_metric = sum(
        board[king_pos[0] + dx, king_pos[1] + dy]== 'B' or features[king_pos[0] + dx, king_pos[1] + dy] in ('C', 'T')
        for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)] if 0<=king_pos[0] + dx<=8 and  0<=king_pos[1] + dy<=8
    )

    # 4. Available escapes: escapes not adjacent to enemies or blocked
    available_escapes = [
    escape for escape in escapes
    if board[escape[0]][escape[1]] == '-' and not (
        # Check the square opposite the board edge
        (escape[0] == 0 and board[1][escape[1]] == 'B') or  # Escape at top edge
        (escape[0] == 8 and board[7][escape[1]] == 'B') or  # Escape at bottom edge
        (escape[1] == 0 and board[escape[0]][1] == 'B') or  # Escape at left edge
        (escape[1] == 8 and board[escape[0]][7] == 'B')     # Escape at right edge
    )
]



    # 5. Free paths from King to escape
    free_paths = free_path_to_escape(king_pos, board)

    # Return metrics as a dictionary
    return np.array([piece_difference,
                king_escape_distance,
                danger_metric,
                len(available_escapes),
                len(free_paths)])

# Compute metrics
board = np.array([
    ['-', '-', '-', 'B', 'B', 'B', '-', '-', '-'],
    ['-', '-', '-', '-', 'B', '-', '-', '-', '-'],
    ['-', '-', '-', '-', 'W', '-', '-', '-', '-'],
    ['B', '-', '-', '-', 'W', '-', '-', '-', 'B'],
    ['B', 'B', 'W', 'W', 'K', 'W', 'W', 'B', 'B'],
    ['B', '-', '-', '-', 'W', '-', '-', '-', 'B'],
    ['-', '-', '-', '-', 'W', '-', '-', '-', '-'],
    ['-', '-', '-', '-', 'B', '-', '-', '-', '-'],
    ['-', '-', '-', 'B', 'B', 'B', '-', '-', '-']
])

print(board_metrics(board))