package Model;

public class Data {
        private final byte[] left = new byte[32];
        private final byte[] right = new byte[32];

        public Data(byte[] binaryData) {
            // 6: Initial permutation, permutacja początkowa (blok 64-bitowy na blok 64-bitowy)
            byte[] initialPermutation = Functions.Permutation(Tables.IP, binaryData, 64);

            // Podział na bloki 32-bitowe
            System.arraycopy(initialPermutation, 0, left, 0, 32);
            System.arraycopy(initialPermutation, 32, right, 0, 32);
        }

        public byte[] getLeft() {
            return left;
        }

        public byte[] getRight() {
            return right;
        }
}
