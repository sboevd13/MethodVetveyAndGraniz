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
    for (i in matrix.indices) {
        for (j in matrix[i].indices) {
            print("${matrix[i][j]} ")
        }
        println("")
    }

}

fun mvg(matrix: Array<IntArray>) : Int {
    val priorityQueue = PriorityQueue<Node>()
    var reductionMatrixH = reductionMatrixAndFindLocalLowBorder(matrix, 0)
    val root = Node(matrix, reductionMatrixH)
    var delEl = findMaxShtraf(matrix)
    root.left = Node(matrix.copy(), reductionMatrixH+delEl[2], true)
    priorityQueue.add(root.left)
    matrix[delEl[1]][delEl[0]] = Int.MAX_VALUE
    root.right = Node(delRowAndColumn(matrix, delEl[0], delEl[1]), reductionMatrixAndFindLocalLowBorder(matrix, reductionMatrixH), false)
    priorityQueue.add(root.right)
    var curr: Node
    while (true) {
        curr = priorityQueue.poll()
        if(curr.isExcluded == true) {
            curr.matrix[delEl[0]][delEl[1]] = Int.MAX_VALUE
            reductionMatrixH = reductionMatrixAndFindLocalLowBorder(curr.matrix, curr.h)
            delEl = findMaxShtraf(curr.matrix)
            curr.left = Node(curr.matrix.copy(), reductionMatrixH, true)
            priorityQueue.add(curr.left)
            curr.matrix[delEl[1]][delEl[0]] = Int.MAX_VALUE
            curr.right = Node(delRowAndColumn(curr.matrix, delEl[0], delEl[1]), reductionMatrixAndFindLocalLowBorder(curr.matrix, curr.h), false)
            priorityQueue.add(curr.right)
        }else {
            delEl = findMaxShtraf(curr.matrix)
            curr.left = Node(curr.matrix.copy(), curr.h + delEl[2], true)
            priorityQueue.add(curr.left)
            curr.matrix[delEl[1]][delEl[0]] = Int.MAX_VALUE
            curr.right = Node(delRowAndColumn(curr.matrix, delEl[0], delEl[1]), reductionMatrixAndFindLocalLowBorder(curr.matrix, curr.h), false)
            priorityQueue.add(curr.right)
        }
        /*
        if(left != null && right != null) {
            if(left.h < right.h) {
                left.matrix[delEl[0]][delEl[1]] = Int.MAX_VALUE
                reductionMatrixH = reductionMatrixAndFindLocalLowBorder(left.matrix)
                delEl = findMaxShtraf(left.matrix)
                left.left = Node(left.matrix, reductionMatrixH)
                left.matrix[delEl[1]][delEl[0]] = Int.MAX_VALUE
                left.right = Node(delRowAndColumn(left.matrix.copy(), delEl[0], delEl[1]), reductionMatrixAndFindLocalLowBorder(left.matrix))
                left = left.left
                right = left.right
            }else{

            }
          */
        }

    return 0
}

fun Array<IntArray>.copy() = map { it.clone() }.toTypedArray()

fun reductionMatrixAndFindLocalLowBorder(matrix: Array<IntArray>, h: Int): Int {
    val di = IntArray(matrix.size)
    val dj = IntArray(matrix.size)
    for (i in matrix.indices) {
        di[i] = matrix[i].min()
        for (j in matrix[i].indices) {
            if(matrix[i][j] != Int.MAX_VALUE){
                matrix[i][j] -= di[i]
            }
        }
    }
    for (j in matrix[0].indices) {
        dj[j] = minInColumn(matrix, j)
        for (i in matrix.indices) {
            if(matrix[i][j] != Int.MAX_VALUE){
                matrix[i][j] -= dj[j]
            }
        }
    }
    var currH = h
    for (i in di.indices){
        currH += di[i] + dj[i]
    }
    return currH
}

fun minInColumn (matrix: Array<IntArray>, j: Int): Int {
    var min = matrix[0][j]
    for (arr in matrix){
        if(arr[j] < min) min = arr[j]
    }
    return min
}

fun findMaxShtraf(matrix: Array<IntArray>) : IntArray {
    var maxShtraf = 0
    var currentShtraf: Int = 0
    var findI: Int = 0
    var findJ: Int = 0
    for(i in matrix.indices) {
        for(j in matrix[i].indices) {
            if(matrix[i][j] == 0) {
                currentShtraf = findShtraf(matrix, i, j)
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

fun findShtraf (matrix: Array<IntArray>, i: Int, j: Int): Int {
    var minInRow = Int.MAX_VALUE
    var minInColumn = Int.MAX_VALUE
    for(k in matrix[i].indices) {
        if(k == j) continue
        if(matrix[i][k] < minInRow) minInRow = matrix[i][k]
    }
    for (k in matrix.indices){
        if(k == i) continue
        if(matrix[k][j] < minInColumn) minInColumn = matrix[k][j]
    }
    return minInRow + minInColumn
}

fun delRowAndColumn(matrix: Array<IntArray>, i: Int, j: Int): Array<IntArray> {

    return matrix.map {
        row -> row.filterIndexed {index, _ -> index != j}.toIntArray()
    }.filterIndexed { index, _ -> index != i }.toTypedArray()
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
/*
data class MatrixAndBorder(val matrix: Array<IntArray>, val border: Int)

 */