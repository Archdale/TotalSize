import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Computes the total size of a folder and its sub-folders and files.
 */
public class TotalSize
{

   private static final String[] ERR_MSG  = { "\nIncorrect Arguments",
         "\nFile/Folder not found.", "Some Files or Folders were unreadable." };
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
    * @return the number of bytes in the folder, if negative
    */
   private static long computeSize(File root)
   {
      File currentNode;

      long totalSize = 0;
      //
      int fileWasSkipped = 1;

      Deque<File> nodeStack = new ArrayDeque<File>();

      // Prime the while loop by putting the root on the stack
      nodeStack.push(root);

      while (!nodeStack.isEmpty())
      {
         currentNode = nodeStack.pop();
         totalSize += currentNode.length();

         /*
          * Try/Catch checks for skipped folders/files due to permissions level
          * Rather than error out, fileWasSkipped is changed to a negative 1, so
          * that when we exit the method, we multiply the value by it. This way
          * the function that called us will know the value of size is
          * incomplete, and the user can be notified
          */
         try
         {
            /*
             * If the node is a directory, we'll push all its contents onto the
             * stack
             */
            if (currentNode.isDirectory())
            {
               for (File child : currentNode.listFiles())
               {
                  nodeStack.push(child);
               }
            }
         }
         catch (NullPointerException e)
         {
            fileWasSkipped = -1;
         }
      }

      return totalSize * fileWasSkipped;
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
    * @param size
    *           The size of the files and folders in the path.
    */
   private static void printSize(String path, long size)
   {
      System.out.printf("Size of %s%n", path);
      if (size < 0)
      {
         System.out.println(ERR_MSG[2]);
      }

      // Removes the negative sign
      size = Math.abs(size);

      // Always print the value in bytes, and appropriately print the others.
      System.out.printf("%,d Bytes%n", size);
      if (size >= KILOBYTE)
      {
         System.out.printf("%,d KB%n", size / KILOBYTE);
      }

      if (size >= MEGABYTE)
      {
         System.out.printf("%,d MB%n", size / MEGABYTE);
      }

      if (size >= GIGABYTE)
      {
         System.out.printf("%,.2f GB%n", size / GIGABYTE);
      }


   }
}
