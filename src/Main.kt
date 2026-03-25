import java.util.PriorityQueue

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    val m = Int.MAX_VALUE
    val matrix = arrayOf(
        intArrayOf(m, 20, 18, 12, 8),
        intArrayOf(5, m, 14, 7, 11),
        intArrayOf(12, 18, m, 6, 11),
        intArrayOf(11, 17, 11, m, 12),
        intArrayOf(5, 5, 5, 5, m)
    )
    println(mvg(matrix))

}

fun mvg(matrix: Array<IntArray>) : Int {
    val priorityQueue = PriorityQueue<Node>()
    var root = Node(matrix,0)
    var reductionMatrixH = reductionMatrixAndFindLocalLowBorder(root)
    root = Node(matrix, reductionMatrixH)
    var leftDelEl = findMaxShtraf(root)
    var rightDelEl = leftDelEl
    root.left = Node(matrix.copyAll(), reductionMatrixH+leftDelEl[2], true)
    priorityQueue.add(root.left)
    matrix[rightDelEl[1]][rightDelEl[0]] = Int.MAX_VALUE
    root.right = Node(
        matrix = matrix,
        h = reductionMatrixAndFindLocalLowBorder(root),
        isExcluded = false,
        rowExcleded = root.rowExcleded.copyOf().apply { this[rightDelEl[0]] = true },
        columnExcleded = root.columnExcleded. copyOf().apply { this[rightDelEl[1]] = true })
    priorityQueue.add(root.right)
    var curr: Node
    while (true) {
        curr = priorityQueue.poll()
        if(curr.isExcluded == true) {
            curr.matrix[leftDelEl[0]][leftDelEl[1]] = Int.MAX_VALUE
            reductionMatrixH = reductionMatrixAndFindLocalLowBorder(curr)
            leftDelEl = findMaxShtraf(curr)
            curr.left = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixH,
                isExcluded = true,
                rowExcleded = curr.rowExcleded,
                columnExcleded = curr.columnExcleded
            )
            priorityQueue.add(curr.left)
            curr.matrix[leftDelEl[1]][leftDelEl[0]] = Int.MAX_VALUE
            curr.right = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixAndFindLocalLowBorder(curr),
                isExcluded = false,
                rowExcleded = curr.rowExcleded.copyOf().apply { this[leftDelEl[0]] = true },
                columnExcleded = curr.columnExcleded.copyOf().apply { this[leftDelEl[1]] = true }
            )
            if (isThisEnd(curr.right!!)) break
            priorityQueue.add(curr.right)
        }else {
            rightDelEl = findMaxShtraf(curr)
            curr.left = Node(curr.matrix.copyAll(), curr.h + rightDelEl[2], true)
            priorityQueue.add(curr.left)
            curr.matrix[rightDelEl[1]][rightDelEl[0]] = Int.MAX_VALUE
            curr.right = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixAndFindLocalLowBorder(curr),
                isExcluded = false,
                rowExcleded = curr.rowExcleded.copyOf().apply { this[rightDelEl[0]] = true },
                columnExcleded = curr.columnExcleded.copyOf().apply { this[rightDelEl[1]] = true }
            )
            if (isThisEnd(curr.right!!)) break
            priorityQueue.add(curr.right)
        }
    }

    return curr.h
}

fun Array<IntArray>.copyAll() = map { it.clone() }.toTypedArray()

fun reductionMatrixAndFindLocalLowBorder(node: Node): Int {
    val di = IntArray(node.matrix.size)
    val dj = IntArray(node.matrix.size)
    for (i in node.matrix.indices) {
        if (node.rowExcleded[i]) continue
        di[i] = node.matrix[i].min()
        for (j in node.matrix[i].indices) {
            if (node.columnExcleded[j]) continue
            if(node.matrix[i][j] != Int.MAX_VALUE){
                node.matrix[i][j] -= di[i]
            }
        }
    }
    for (j in node.matrix[0].indices) {
        if (node.columnExcleded[j]) continue
        dj[j] = minInColumn(node, j)
        for (i in node.matrix.indices) {
            if (node.rowExcleded[i]) continue
            if(node.matrix[i][j] != Int.MAX_VALUE){
                node.matrix[i][j] -= dj[j]
            }
        }
    }
    var currH = node.h
    for (i in di.indices){
        currH += di[i] + dj[i]
    }
    return currH
}

fun minInColumn (node: Node, j: Int): Int {
    var min = node.matrix[0][j]
    for (arr in node.matrix.indices){
        if (node.rowExcleded[arr]) continue
        if (node.matrix[arr][j] < min) min = node.matrix[arr][j]
    }
    return min
}

fun findMaxShtraf(node: Node) : IntArray {
    var maxShtraf = 0
    var currentShtraf: Int = 0
    var findI: Int = 0
    var findJ: Int = 0
    for(i in node.matrix.indices) {
        if (node.rowExcleded[i]) continue
        for(j in node.matrix[i].indices) {
            if (node.columnExcleded[j]) continue
            if(node.matrix[i][j] == 0) {
                currentShtraf = findShtraf(node, i, j)
                if(currentShtraf > maxShtraf) {
                    maxShtraf = currentShtraf
                    findI = i
                    findJ = j
                }
            }
        }
    }
    return intArrayOf(findI, findJ, maxShtraf)
}

fun findShtraf (node: Node, i: Int, j: Int): Int {
    var minInRow = Int.MAX_VALUE
    var minInColumn = Int.MAX_VALUE
    for(k in node.matrix[i].indices) {
        if(k == j || node.columnExcleded[k]) continue
        if(node.matrix[i][k] < minInRow) minInRow = node.matrix[i][k]
    }
    for (k in node.matrix.indices){
        if(k == i || node.rowExcleded[k]) continue
        if(node.matrix[k][j] < minInColumn) minInColumn = node.matrix[k][j]
    }
    return minInRow + minInColumn
}

fun isThisEnd (node: Node): Boolean {
    var count = 0
    for (value in node.rowExcleded) {
        if (value == false) count++
    }
    return count == 1
}


data class Node  (
    val matrix: Array<IntArray>,
    val h: Int,
    val isExcluded: Boolean? = null,
    var left: Node? = null,
    var right: Node? = null,
    var rowExcleded: BooleanArray = BooleanArray(matrix.size) {false},
    var columnExcleded: BooleanArray = BooleanArray(matrix.size) {false}
) : Comparable<Node> {
    override fun compareTo(other: Node): Int {
        return compareValuesBy(this, other, Node::h)
    }
}
