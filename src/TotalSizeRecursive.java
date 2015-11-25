import java.io.File;


/**
 * Computes the total size of a folder and its sub-folders and files.
 */
public class TotalSizeRecursive
{
   private static final String[] ERR_MSG  = { "\nIncorrect Arguments",
         "\nFile/Folder not found.",
         "\n%,d Files or Folders were unreadable.%n" };
   private static final String   USAGE    = "java TotalSize <Path Name>";
   private static final int      KILOBYTE = 1024;
   private static final int      MEGABYTE = 1024 * 1024;
   private static final float    GIGABYTE = 1024 * 1024 * 1024;


   /**
    * Computes the size of all sub-folders and files at a specific path
    * 
    * @param args
    *           the first and only argument is the file path to compute the size
    *           of
    */
   public static void main(String[] args)
   {
      /*
       * Check to make sure there's only one argument, if not, we print a usage
       * message
       */
      if (args.length != 1)
      {
         System.err.println(ERR_MSG[0]);
         System.out.println(USAGE);
      }

      else
      {
         File root = new File(args[0]);

         /*
          * Check to see if the path name is a valid file; if not, we return the
          * an error message and exit gracefully. Otherwise we compute the size
          * and print it.
          */
         if (!root.exists())
         {
            System.err.println(ERR_MSG[1]);
         }
         else
         {
            printSize(args[0], computeSize(root));
         }
      }
   }


   /**
    * Computes the number of bytes in a folder, its sub-folder, and there files.
    * If the returned value is negative, it means that there were files or
    * sub-folders that mean that some files or folders were unable to be read.
    * 
    * @param root
    *           the folder to start computing from.
    * @return an array of long values, index 0 holds the size computed, and
    *         index 1 holds how many files or folders were unreadable
    */
   private static Result computeSize(File root)
   {

      Result totalSize = new Result();
      totalSize._size = 0;
      totalSize._skipped = 0;

      /*
       * Base Case is if the File is a file and not a directory. Also could have
       * checked for listFiles returning an empty list.
       */
      if (root.isFile())
      {
         totalSize._size = root.length();
         return totalSize;
      }

      /*
       * Every other case works down the tree to the leaves(files). If it skips
       * a file or folder, we keep track of it in the index 1 of totalSize.
       */
      else
      {
         totalSize._size += root.length();
         for (File children : root.listFiles())
         {
            try
            {
               totalSize._size += computeSize(children)._size;
            }
            catch (NullPointerException e)
            {
               totalSize._skipped += 1;
            }
         }
      }

      return totalSize;
   }


   /**
    * Prints the path and its size in Bytes, KB, MB, and GB.
    * 
    * Appropriately removes printouts based on the size passed. Also notifies
    * the user if some files were skipped due to being unable to read them
    * because of permissions.
    * 
    * @param path
    *           The path that had its size computed.
    * @param result
    *           The first index is size of the files and folders in the path,
    *           the second index is the number of files and folder skipped due
    *           to permissions.
    */
   private static void printSize(String path, Result result)
   {
      System.out.printf("Size of %s%n", path);
      if (result._skipped > 0)
      {
         System.out.printf(ERR_MSG[2], result._skipped);
      }

      // Always print the value in bytes, and appropriately print the others.
      System.out.printf("%,d Bytes%n", result._size);
      if (result._size >= KILOBYTE)
      {
         System.out.printf("%,d KB%n", result._size / KILOBYTE);
      }

      if (result._size >= MEGABYTE)
      {
         System.out.printf("%,d MB%n", result._size / MEGABYTE);
      }

      if (result._size >= GIGABYTE)
      {
         System.out.printf("%,.2f GB%n", result._size / GIGABYTE);
      }


   }
   
   private static class Result
   {
      long _size;
      int _skipped;
      Result()
      {
         _size = 0;
               _skipped = 0;
      }
   }
}
